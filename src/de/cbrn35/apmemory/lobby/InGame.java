package de.cbrn35.apmemory.lobby;

import java.util.concurrent.ExecutionException;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import de.cbrn35.apmemory.C;
import de.cbrn35.apmemory.Game;
import de.cbrn35.apmemory.HttpAsyncTask;
import de.cbrn35.apmemory.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;
//TODO
//Layout gestalten (res/layout/ingame.xml)
//Spielerliste laden
//Spielfeld generieren = ImageViews erzeugen
//Options-Menü erzeugen (Statistik, Spiel verlassen)
//Statistik-Dialog erzeugen
//Spiel-Logik

public class InGame extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ingame);

		GridView gridview = (GridView) findViewById(R.id.gv_ingame);

		int gameid = getIntent().getExtras().getInt("gameid");
		int playerid = getIntent().getExtras().getInt("playerid");

		HttpGet getGame = new HttpGet(C.URL + "?action=get_game&gameid="
				+ gameid);
		HttpAsyncTask getGameTask = new HttpAsyncTask(getGame, this, null,
				false);
		getGameTask.execute();

		JSONObject response;
		Game game;
		try {
			response = getGameTask.get();
			game = new Game(response.getJSONObject("data"));

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		gridview.setNumColumns(4);
		gridview.setAdapter(new ImageAdapter(this));		

		/*
		 * gridview.setNumColumns(8); gridview.setAdapter(new
		 * ImageAdapter8x8(this));
		 */
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
		case R.id.gamelobby_start:
			Toast.makeText(this, "Noch nicht implementiert!", Toast.LENGTH_LONG)
					.show();
			break;
		case R.id.gamelobby_leave:
			Toast.makeText(this, "Noch nicht implementiert!", Toast.LENGTH_LONG)
					.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
