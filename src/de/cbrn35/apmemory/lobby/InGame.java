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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
//TODO
//Layout gestalten (res/layout/ingame.xml)
//Spielerliste laden
//Spielfeld generieren = ImageViews erzeugen
//Options-Menü erzeugen (Statistik, Spiel verlassen)
//Statistik-Dialog erzeugen
//Spiel-Logik

public class InGame extends Activity {
	private Handler refreshHandler = new Handler();
	private boolean isInLoop = false;
	private Game game;
	private Player player;
	private ArrayList<Player> playerList = new ArrayList<Player>();
	public GridView gridview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ingame);

		gridview = (GridView) findViewById(R.id.gv_ingame);

		int gameid = getIntent().getExtras().getInt("gameid");
		
		// get initial game object
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
		
		gridview.setNumColumns(4);
		gridview.setAdapter(new ImageAdapter(this, game));		

		/*
		 * gridview.setNumColumns(8); gridview.setAdapter(new
		 * ImageAdapter8x8(this));
		 */
	}
	
	@Override
	public void onBackPressed() {
		Toast.makeText(this, getResources().getString(R.string.ingame_leave_info), Toast.LENGTH_LONG)
		.show();
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.ingame_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ingame_statistik:
			Toast.makeText(this, "Noch nicht implementiert!", Toast.LENGTH_LONG)
					.show();
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
	
	public synchronized void refreshGameStatus() {
		// get current game status
		HttpGet getGame = new HttpGet(C.URL + "?action=check_game&gameid="+game.id);
		HttpAsyncTask getGameTask = new HttpAsyncTask(getGame, this, null, false);
		getGameTask.execute();
		
		try {
			JSONObject gameResult = getGameTask.get();
			if(gameResult.getInt("error") == 0) {
				// update local game object
				game = new Game(gameResult.getJSONObject("data").getJSONObject("game"));
				
				// get player list
				JSONArray players = gameResult.getJSONObject("data").getJSONArray("players");
				
				ArrayList<Player> currPList = new ArrayList<Player>();
				
				// compare online player list with current local player list
				for(int i = 0; i < players.length(); i++) {
					Player p = new Player(players.getJSONObject(i));
					currPList.add(p);
					if(!playerList.contains(p)) {
						playerList.add(p);
					}
					
					// update player object
					if(p.id == player.id) {
						new PlayerSQLiteDAO(this).persist(p);
						player = p;
					}
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
		if(game != null) {
			HttpGet getLeave = new HttpGet(C.URL + "?action=leave_game&user=" + player.username + "&gameid=" + game.id);
			HttpAsyncTask leaveTask = new HttpAsyncTask(getLeave, this, null, false);
			leaveTask.execute();
		}
		finish();
	}
	
	private Runnable runnableRefresh = new Runnable() {
		public void run() {
			refreshGameStatus();
			if(isInLoop) {
				refreshHandler.postDelayed(this, 5000);
			}
		}
	};
}
