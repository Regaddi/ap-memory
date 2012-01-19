package de.cbrn35.apmemory.lobby;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import de.cbrn35.apmemory.C;
import de.cbrn35.apmemory.Game;
import de.cbrn35.apmemory.GameField;
import de.cbrn35.apmemory.HttpAsyncTask;
import de.cbrn35.apmemory.Player;
import de.cbrn35.apmemory.R;
import de.cbrn35.apmemory.Stats;

public class Lobby extends Activity {
	public final static String I_GETOPENGAMES = "getOpenGames"; 
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lobby);
		
		refreshLobby();
	}
	
	protected void onResume() {
		super.onResume();
		refreshLobby();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.lobby_menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.lobby_menu_new_game:
			Intent i = new Intent(this, CreateGame.class);
			startActivity(i);
			break;
		case R.id.lobby_menu_refresh:
			refreshLobby();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void refreshLobby() {
		HttpGet getOpenGames = new HttpGet(C.URL + "?action=get_open_games");
		HttpAsyncTask refreshTask = new HttpAsyncTask(getOpenGames, this, null);
		refreshTask.execute();
		try {
			JSONObject result = refreshTask.get();
			generateRecentOpenGamesList(result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		TextView statsWon = (TextView)findViewById(R.id.lobby_stats_won);
		TextView statsLost = (TextView)findViewById(R.id.lobby_stats_lost);
		
		Player p = new PlayerSQLiteDAO(this).getPlayer();
		
		statsWon.setText(p.stats.won+"");
		statsLost.setText(p.stats.lost+"");
	}
	
	public void generateRecentOpenGamesList(JSONObject result) {
		ListView lv_open_games = (ListView)findViewById(R.id.lv_lobby_recent_open_games);
		ArrayList<Game> listOpenGames = new ArrayList<Game>();
		try {
			for(int i = 0; i < result.getJSONArray("data").length(); i++) {
				JSONObject j = result.getJSONArray("data").getJSONObject(i);
				Game g = new Game(
					j.getInt("id"),
					j.getString("name"),
					new Player(
						j.getJSONObject("creator").getInt("id"),
						j.getJSONObject("creator").getString("username"),
						j.getJSONObject("creator").getString("email"),
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(j.getJSONObject("creator").getString("lastActivity")),
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(j.getJSONObject("creator").getString("signupDate")),
						j.getJSONObject("creator").getInt("currentScore"),
						j.getJSONObject("creator").getInt("currentGameId"),
						new Stats(
							j.getJSONObject("creator").getJSONObject("stats").getInt("won"),
							j.getJSONObject("creator").getJSONObject("stats").getInt("lost"),
							j.getJSONObject("creator").getJSONObject("stats").getInt("pickSuccess"),
							j.getJSONObject("creator").getJSONObject("stats").getInt("pickFail")
						)
					),
					j.getInt("status"),
					j.getInt("gameSize"),
					new GameField(
						j.getJSONObject("gameField")
					)
				);
				listOpenGames.add(g);
			}
			lv_open_games.setAdapter(new OpenGameAdapter(this, R.layout.open_games_list_item, listOpenGames));
			lv_open_games.setOnItemClickListener(new OnItemClickListener() {
			    public void onItemClick(AdapterView<?> parent, View view,
			        int position, long id) {
			    	int gameId = Integer.parseInt(((TextView)view.findViewById(R.id.open_game_id)).getText().toString());
			    	Player p = new PlayerSQLiteDAO(view.getContext()).getPlayer();
			    	HttpGet joinGet = new HttpGet(C.URL + "?action=join_game&user="+p.username+"&gameid="+gameId);
			    	Intent success = new Intent(view.getContext(), GameLobby.class);
			    	success.putExtra("gameid", "{\"data\": \""+gameId+"\"}");
			    	new HttpAsyncTask(joinGet, view.getContext(), success).execute();
			    }
			});
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
