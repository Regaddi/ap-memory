package de.cbrn35.apmemory.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import de.cbrn35.apmemory.*;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;


public class ImageAdapter extends BaseAdapter {
	 private Context mContext;
	 private Game game;
	 private GameField gf;
	 private HashMap<Integer, Integer> posCardRelations = new HashMap<Integer, Integer>();
	 private Integer hiddenCard = R.drawable.card_unknown;
	 //GridView myGridView;
	 public ImageAdapter(Context c, Game g) {
	        mContext = c;
	        this.game = g;
	        this.gf = g.gameField;
	        
	        ArrayList<Integer> usedPics = new ArrayList<Integer>();
	        
	        for(Card card : this.gf.cards) {
	        	Integer res;
	        	do {
	        		res = mThumbIds[(int)Math.abs(Math.random()*mThumbIds.length)];
	        	} while(usedPics.contains(res));
	        	
	        	usedPics.add(res);
	        	
	        	posCardRelations.put(card.pos1, res);
	        	posCardRelations.put(card.pos2, res);
	        }
	        
	        //myGridView = new GridView(c);
	        //myGridView.setNumColumns(4);
			
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
            //imageView.setLayoutParams(New GridView.)
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setPadding(4, 4, 4, 4);
            imageView.setAdjustViewBounds(true);
            
            imageView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Player p = new PlayerSQLiteDAO(mContext).getPlayer();
					if(p.id == game.currentPlayer.id) {
						int position = (Integer)((ImageView)v).getTag();
						HttpGet turnCardGet = new HttpGet(C.URL+"?action=turn_card&gameid="+game.id+"&user="+p.username+"&position="+position);
						HttpAsyncTask turnTask = new HttpAsyncTask(turnCardGet, mContext, null, false);
						turnTask.execute();
						
						try {
							JSONObject result = turnTask.get();
							if(result.getInt("error") == 0) {
								game = new Game(result.getJSONObject("data").getJSONObject("game"));
								gf = game.gameField;
								for(Card c : gf.cards) {
									// handle pos 1
									ImageView img = getItem(c.pos1);
									if(c.visible1) {
										int r = posCardRelations.get(c.pos1);
										Log.i(C.LOGTAG, "visible at position "+c.pos1+", res: "+r);
										img.setImageResource(r);
										img.setImageDrawable(mContext.getResources().getDrawable(r));
									} else {
										img.setImageResource(hiddenCard);
										img.setImageDrawable(mContext.getResources().getDrawable(hiddenCard));
									}
									img.forceLayout();
									img.invalidate();
									img.postInvalidate();
									img.refreshDrawableState();
									// handle pos 2
									img = getItem(c.pos2);
									if(c.visible2) {
										int r = posCardRelations.get(c.pos2);
										Log.i(C.LOGTAG, "visible at position "+c.pos2+", res: "+r);
										img.setImageResource(r);
										img.setImageDrawable(mContext.getResources().getDrawable(r));
									} else {
										img.setImageResource(hiddenCard);
										img.setImageDrawable(mContext.getResources().getDrawable(hiddenCard));
									}
									img.forceLayout();
									img.invalidate();
									img.postInvalidate();
									img.refreshDrawableState();
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

}
