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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GameLobby extends Activity {
	Game game = null;
	Player player = null;
	ArrayList<Player> players = new ArrayList<Player>();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gamelobby);
		
		if(getIntent().hasExtra("data") || getIntent().hasExtra("gameid")) {
			player = new PlayerSQLiteDAO(this).getPlayer();
			
			try {
				int gameid = 0;
				if(getIntent().hasExtra("gameid"))
					gameid = new JSONObject(getIntent().getExtras().getString("gameid")).getInt("data");
				else if(getIntent().hasExtra("data"))
					gameid = new JSONObject(getIntent().getExtras().getString("data")).getInt("data");
				HttpGet joinGet = new HttpGet(C.URL + "?action=join_game&user=" + Uri.encode(player.username) + "&gameid=" + gameid);
				HttpAsyncTask joinTask = new HttpAsyncTask(joinGet, this, null);
				joinTask.execute();
				JSONObject result = joinTask.get();
				game = new Game(result.getJSONObject("data"));
				initGameLobby();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, getResources().getString(R.string.err_no_game), Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.gamelobby_menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.gamelobby_start:
			Toast.makeText(this, "Noch nicht implementiert!", Toast.LENGTH_LONG).show();
			break;
		case R.id.gamelobby_leave:
			HttpGet getLeave = new HttpGet(C.URL + "?action=leave_game&user=" + player.username + "&gameid=" + game.id);
			HttpAsyncTask leaveTask = new HttpAsyncTask(getLeave, this, null);
			leaveTask.execute();
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void refreshPlayers() {
		ListView lv_players = (ListView)findViewById(R.id.gamelobby_players);
		
		HttpGet getPlayers = new HttpGet(C.URL + "?action=list_players&gameid=" + game.id);
		HttpAsyncTask listPlayersTask = new HttpAsyncTask(getPlayers, this, null);
		listPlayersTask.execute();
		
		try {
			JSONObject result = listPlayersTask.get();
			JSONArray jPlayers = result.getJSONObject("data").getJSONArray("players");
			players.clear();
			for(int i = 0; i < jPlayers.length(); i++) {
				Player p = new Player(jPlayers.getJSONObject(i));
				players.add(p);
			}
			
			lv_players.setAdapter(new PlayerAdapter(this, R.layout.player_list_item, players));
			lv_players.forceLayout();
			
			for(int i = 0; i < lv_players.getChildCount(); i++) {
				int id = Integer.parseInt(((TextView)lv_players.getChildAt(i).findViewById(R.id.player_id)).getText().toString());
				if(id == game.creator.id) {
					Log.i(C.LOGTAG, lv_players.getChildAt(i).toString());
					((ImageView)lv_players.getChildAt(i).findViewById(R.id.player_is_creator)).setVisibility(View.VISIBLE);
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void initGameLobby() {
		TextView tv_name = (TextView)findViewById(R.id.gamelobby_name);
		ImageView iv_size = (ImageView)findViewById(R.id.gamelobby_size_image);
		
		tv_name.setText(game.name);
		switch(game.gameSize) {
		case 8:
			iv_size.setImageResource(R.drawable.game_size_8);
			break;
		case 16:
			iv_size.setImageResource(R.drawable.game_size_16);
			break;
		case 32:
			iv_size.setImageResource(R.drawable.game_size_32);
			break;
		}
		
		refreshPlayers();
	}
}
