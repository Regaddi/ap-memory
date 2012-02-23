package de.cbrn35.apmemory.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import de.cbrn35.apmemory.*;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * 
 * represents the current gamefield
 *
 */

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private Game game;
	private GameField gf;
	private HashMap<Integer, Integer> posCardRelations = new HashMap<Integer, Integer>();
	private Integer hiddenCard = R.drawable.card_unknown;
	private Handler handler = new Handler();
	
	
	public ImageAdapter(Context c, Game g) {
		mContext = c;
		this.game = g;
		this.gf = g.gameField;
		
		ArrayList<Integer> usedPics = new ArrayList<Integer>();
		//spread cards over the gamefield
		for(Card card : this.gf.cards) {
			Integer res;
			do {
				res = mThumbIds[(int)Math.abs(Math.random()*mThumbIds.length)];
			} while(usedPics.contains(res));
			
			usedPics.add(res);
			
			posCardRelations.put(card.pos1, res);
			posCardRelations.put(card.pos2, res);
		}
		
	}
	
	public void setGameObject(Game g) {
		this.game = g;
		this.gf = g.gameField;
		notifyDataSetChanged();
	}
	
	public int getCount() {
		return posCardRelations.size();
	}

	public ImageView getItem(int position) {
		return (ImageView)getView(position, null, null);
		//return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setPadding(4, 4, 4, 4);
            imageView.setAdjustViewBounds(true);
            
            imageView.setOnClickListener(new OnClickListener() {
            	//onClick for the Gamefield
				public void onClick(View v) {
					Log.i(C.LOGTAG, game.toString());
					Player p = new PlayerSQLiteDAO(mContext).getPlayer();
					
					InGame ig = (InGame)v.getContext();
					ig.refreshHandler.removeCallbacks(ig.forceSkip);
					ig.forceSkipped = 0;
					
					Integer clickedPosition = (Integer)v.getTag();
					
					Card clickedCard = gf.findCard(clickedPosition);
					if(
						(clickedCard.pos1 == clickedPosition
							&& clickedCard.visible1)
						||
						(clickedCard.pos2 == clickedPosition
							&& clickedCard.visible2)
					) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.ingame_already_turned), Toast.LENGTH_LONG).show();
						return;
					}
					
					if(p.id == game.currentPlayer.id) {
						// count visible non-paired images
						int visible = 0;
						for(Card c : gf.cards) {
							if(c.visible1 && !c.paired) visible++;
							if(c.visible2 && !c.paired) visible++;
						}
						
						if(visible == 0) {
							ig.refreshHandler.postDelayed(ig.forceSkip, ig.forceSkipDelay);
						}
						
						if(visible < 2) {
							int position = (Integer)((ImageView)v).getTag();
							HttpGet turnCardGet = new HttpGet(C.URL+"?action=turn_card&gameid="+game.id+"&user="+p.username+"&position="+position);
							HttpAsyncTask turnTask = new HttpAsyncTask(turnCardGet, mContext, null, false);
							turnTask.execute();
							
							try {
								JSONObject result = turnTask.get();
								if(result.getInt("error") == 0) {
									game = new Game(result.getJSONObject("data").getJSONObject("game"));
									gf = game.gameField;
									for(Card card : gf.cards) {
										((Activity)v.getContext()).runOnUiThread(new ImgRunnable(card));
									}
									
									if(visible == 1) {
										checkTurnStatus();
									}
								} else {
									Toast.makeText(mContext, result.getString("error_msg"), Toast.LENGTH_LONG).show();
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
								Toast.makeText(mContext, mContext.getResources().getString(R.string.err_no_connection), Toast.LENGTH_LONG).show();
							} catch (ExecutionException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.ingame_not_your_turn), Toast.LENGTH_LONG).show();
					}
				}
			});
        } else {
            imageView = (ImageView) convertView;
        }
        Card card = gf.findCard(position);
        if(card != null) {
        	imageView.setTag(position);
        	if(card.pos1 == position) {
        		if(card.visible1) {
        			imageView.setImageResource(posCardRelations.get(position));
        		} else {
        			imageView.setImageResource(hiddenCard);
        		}
        	}
        	if(card.pos2 == position) {
        		if(card.visible2) {
        			imageView.setImageResource(posCardRelations.get(position));
	    		} else {
	    			imageView.setImageResource(hiddenCard);
	    		}
        	}
        	if(card.visible1 && card.visible2 && card.paired) {
        		imageView.setAlpha(128);
        	} else {
        		imageView.setAlpha(255);
        	}
        } else {
        	Log.e(C.LOGTAG, "Card not found at position "+position);
        }
        return imageView;
	}
	
	private Integer[] mThumbIds = {
			R.drawable.meme_img00,R.drawable.meme_img01,
			R.drawable.meme_img02,R.drawable.meme_img03,
			R.drawable.meme_img04,R.drawable.meme_img05,
			R.drawable.meme_img06,R.drawable.meme_img07,
			R.drawable.meme_img08,R.drawable.meme_img09,
			R.drawable.meme_img10,R.drawable.meme_img11,
			R.drawable.meme_img12,R.drawable.meme_img13,
			R.drawable.meme_img14,R.drawable.meme_img15,
    };
	
	public void checkTurnStatus() {
		Log.i(C.LOGTAG, "Checking turnstatus...");
		
		// count visible non-paired images
		int visible = 0;
		for(Card c : gf.cards) {
			if(c.visible1 && !c.paired) visible++;
			if(c.visible2 && !c.paired) visible++;
		}
		
		Log.i(C.LOGTAG, visible+" Karten derzeit aufgedeckt");
		
		if(visible == 2) {
			// 2 cardturns done... send finish turn to server after delay
			final Runnable task = new Runnable() {
	            public void run() {
	                Log.i(C.LOGTAG, "Starte Timertask");
	        		Player p = new PlayerSQLiteDAO(mContext).getPlayer();
	        		HttpGet finishTurnGet = new HttpGet(C.URL+"?action=finish_turn&gameid="+game.id+"&user="+p.username);
					HttpAsyncTask finishTask = new HttpAsyncTask(finishTurnGet, mContext, null, false);
					finishTask.execute();
					try {
						JSONObject finishResult = finishTask.get();
						if(finishResult.getInt("error") == 0) {
							game = new Game(finishResult.getJSONObject("data").getJSONObject("game"));
							gf = game.gameField;
							for(Card c : gf.cards) {
								ImageView v = ImageAdapter.this.getItem(0);
								((Activity)v.getContext()).runOnUiThread(new ImgRunnable(c));
							}
							if(game.currentPlayer.id != p.id) {
								((InGame)ImageAdapter.this.getItem(0).getContext()).myTurn = false;
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						Toast.makeText(mContext, mContext.getResources().getString(R.string.err_no_connection), Toast.LENGTH_LONG).show();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
	            }
	        };
	        handler.postDelayed(task, 5000);
		} else {
			checkTurnStatus();
		}
	}
	
	private class ImgRunnable implements Runnable {
		private final Card card;

	    ImgRunnable(final Card card) {
	     	this.card = card;
	    }

	    public void run() {
			// handle pos 1
			ImageView img = getItem(card.pos1);
			if(card.visible1) {
				Integer r = posCardRelations.get(card.pos1);
				Log.i(C.LOGTAG, "visible at position "+card.pos1+", res: "+r);
				img.setImageResource(r);
			} else {
				img.setImageResource(hiddenCard);
			}
			img.invalidate();
			// handle pos 2
			img = getItem(card.pos2);
			if(card.visible2) {
				Integer r = posCardRelations.get(card.pos2);
				Log.i(C.LOGTAG, "visible at position "+card.pos2+", res: "+r);
				img.setImageResource(r);
			} else {
				img.setImageResource(hiddenCard);
			}
			img.invalidate();
			
			notifyDataSetChanged();
		}
	}
}
