package de.cbrn35.apmemory.lobby;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.cbrn35.apmemory.C;
import de.cbrn35.apmemory.Game;
import de.cbrn35.apmemory.HttpAsyncTask;
import de.cbrn35.apmemory.Player;
import de.cbrn35.apmemory.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Represents game logic and event handling 
 */
public class InGame extends Activity {
	public Handler refreshHandler = new Handler();
	private boolean isInLoop = false;
	// contains current game object
	private Game game;
	// contains local player object
	private Player player;
	// contains all players participating at current game
	private ArrayList<Player> playerList = new ArrayList<Player>();
	public GridView gridview;
	public boolean myTurn = false;
	public final static int DIALOG_STATS = 1;
	// counter for timeouts (no action)
	public int forceSkipped = 0;
	public Runnable forceSkip;
	// time in ms for timeout
	public int forceSkipDelay = 20000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ingame);

		gridview = (GridView) findViewById(R.id.gv_ingame);

		// gameid needs to be sent via intent (from gamelobby)
		int gameid = getIntent().getExtras().getInt("gameid");
		
		// get initial game object from server
		HttpGet getGame = new HttpGet(C.URL + "?action=get_game&gameid="+gameid);
		HttpAsyncTask getGameTask = new HttpAsyncTask(getGame, this, null, false);
		getGameTask.execute();
		
		try {
			game = new Game(getGameTask.get().getJSONObject("data"));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		// load player from local db
		player = new PlayerSQLiteDAO(this).getPlayer();
		
		// start refresh handler
		isInLoop = true;
		refreshHandler.postDelayed(runnableRefresh, 100);
		
		// initiate gridview
		gridview.setNumColumns(4);
		gridview.setAdapter(new ImageAdapter(this, game));		
	}
	
	@Override
	public void onBackPressed() {
		// disable hardware back key. show toast instead
		Toast.makeText(this, getResources().getString(R.string.ingame_leave_info), Toast.LENGTH_LONG)
		.show();
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.ingame_menu, menu);
		// skip only, if it's local players turn
		menu.findItem(R.id.ingame_skip).setEnabled(myTurn);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ingame_statistik:
			showDialog(DIALOG_STATS);
			break;
		case R.id.ingame_skip:
			skipTurn();
			break;
		case R.id.ingame_verlassen:
			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
	        alertbox.setTitle(getResources().getString(R.string.gamelobby_leave_title));
	        alertbox.setMessage(getResources().getString(R.string.gamelobby_leave_msg));

	        alertbox.setPositiveButton(getResources().getString(R.string.gamelobby_leave_yes),
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface arg0, int arg1) {
	                        leaveGame();
	                    }
	                });

	        alertbox.setNeutralButton(getResources().getString(R.string.gamelobby_leave_no),
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface arg0, int arg1) {
	                    }
	                });

	        alertbox.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id) {
		case DIALOG_STATS:
			// shows current statistics (current score for each player)
			dialog.setContentView(R.layout.ingame_dialog_stats_layout);
			dialog.setTitle(R.string.ingame_dialog_stats_title);
			
			LinearLayout ll = (LinearLayout)dialog.findViewById(R.id.ingame_dialog_wrapper);
			
			for(Player p: this.playerList) {
				// add horizontal linear layout per player including textviews
				LinearLayout pll = new LinearLayout(this);
				// player name textview
				TextView pTV = new TextView(this);
				pTV.setText(p.username);
				pTV.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
				// player score textview
				TextView pTVScore = new TextView(this);
				pTVScore.setText(p.currentScore+"");
				pTVScore.setGravity(Gravity.RIGHT);
				pll.addView(pTV);
				pll.addView(pTVScore);
				ll.addView(pll);
				if(p.id == player.id && game.status == 2) {
					// save player object, when game is finished
					new PlayerSQLiteDAO(this).persist(p);
				}
			}
			
			if(game.status == 2) {
				// remove automatic refreshs, when game is finished
				refreshHandler.removeCallbacks(runnableRefresh);
				refreshHandler.removeCallbacks(forceSkip);
			}
			
			Button okButton = (Button) dialog.findViewById(R.id.ingame_dialog_button_ok);
			
			okButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(game.status < 2) {
						dismissDialog(DIALOG_STATS);
					} else {
						// leave game, if game is finished
						dismissDialog(DIALOG_STATS);
						refreshHandler.removeCallbacks(runnableRefresh);
						isInLoop = false;
						finish();
					}
				}
			});
			break;
		default:
			dialog = null;
			break;
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch(id) {
		case DIALOG_STATS:
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.ingame_dialog_stats_layout);
			dialog.setTitle(R.string.ingame_dialog_stats_title);
			dialog.setCancelable(true);
			break;
		default:
			dialog = null;
			break;
		}
		return dialog;
	}
	
	public synchronized void refreshGameStatus() {
		// get current game status from server
		HttpGet getGame = new HttpGet(C.URL + "?action=check_game&gameid="+game.id+"&user="+player.username);
		HttpAsyncTask getGameTask = new HttpAsyncTask(getGame, this, null, false);
		getGameTask.execute();
		
		try {
			JSONObject gameResult = getGameTask.get();
			if(gameResult.getInt("error") == 0) {
				// update local game object
				game = new Game(gameResult.getJSONObject("data").getJSONObject("game"));
				
				// reset image adapter
				ImageAdapter ia = (ImageAdapter)gridview.getAdapter();
				ia.setGameObject(game);
				
				// set current player textview
				TextView tvCurrPlayer = (TextView)findViewById(R.id.ingame_curr_player);
				tvCurrPlayer.setText(game.currentPlayer.username+" ist am Zug.");
				
				if(game.status == 2) {
					// game ended, all pairs found
					Toast.makeText(this, getResources().getString(R.string.ingame_game_finished), Toast.LENGTH_LONG).show();
					refreshHandler.removeCallbacks(runnableRefresh);
					isInLoop = false;
					showDialog(DIALOG_STATS);
					return;
				}
				
				// get player list
				JSONArray players = gameResult.getJSONObject("data").getJSONArray("players");
				
				ArrayList<Player> currPList = new ArrayList<Player>();
				
				boolean iAmStillIn = false;
				
				// compare online player list with current local player list
				for(int i = 0; i < players.length(); i++) {
					Player p = new Player(players.getJSONObject(i));
					currPList.add(p);
					
					// update local player object
					if(p.id == player.id) {
						new PlayerSQLiteDAO(this).persist(p);
						player = p;
						iAmStillIn = true;
					}
				}
				
				// leave game, if local player has been terminated by server
				// (timeout e.g.)
				if(!iAmStillIn) {
					game = null;
					leaveGame();
					return;
				}
				
				// check for left users
				if(currPList.size() < playerList.size()) {
					for(int i = 0; i < playerList.size(); i++) {
						Player x = playerList.get(i);
						boolean contains = false;
						for(int j = 0; j < currPList.size(); j++) {
							if(x.id == currPList.get(j).id) contains = true;
						}
						if(!contains) {
							Toast.makeText(this, x.username+" hat das Spiel verlassen.", Toast.LENGTH_LONG).show();
							playerList.remove(x);
						}
					}
				} else {
					playerList = currPList;
				}
				
				// check if it's local players turn
				if(game.currentPlayer.id == player.id && !myTurn) {
					Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					v.vibrate(300);
					Toast.makeText(this, getResources().getString(R.string.ingame_my_turn), Toast.LENGTH_LONG).show();
					myTurn = true;
					
					// trigger force skip
					forceSkip = new Runnable() {
						public void run() {
							if(myTurn) {
								forceSkipped++;
								if(forceSkipped == 3)
									leaveGame();
								else
									skipTurn();
							}
						}
					};
					refreshHandler.postDelayed(forceSkip, forceSkipDelay);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void leaveGame() {
		refreshHandler.removeCallbacks(runnableRefresh);
		isInLoop = false;
		if(game != null) {
			if(myTurn && game.status < 2) {
				skipTurn();
			}
			HttpGet getLeave = new HttpGet(C.URL + "?action=leave_game&user=" + player.username + "&gameid=" + game.id);
			HttpAsyncTask leaveTask = new HttpAsyncTask(getLeave, this, null, false);
			leaveTask.execute();
		}
		finish();
	}
	
	public void skipTurn() {
		HttpGet getSkip = new HttpGet(C.URL + "?action=finish_turn&user=" + player.username + "&gameid=" + game.id);
		HttpAsyncTask skipTask = new HttpAsyncTask(getSkip, this, null, false);
		skipTask.execute();
		myTurn = false;
	}
	
	private Runnable runnableRefresh = new Runnable() {
		public void run() {
			if(isInLoop) {
				refreshGameStatus();
				refreshHandler.postDelayed(this, 5000);
			}
		}
	};
}
