package de.cbrn35.apmemory.lobby;

import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import de.cbrn35.apmemory.C;
import de.cbrn35.apmemory.HttpAsyncTask;
import de.cbrn35.apmemory.R;

public class CreateGame extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.creategame);
		
		// Spielfeldgrößen in Spinner laden
	    Spinner spinMaxSize = (Spinner) findViewById(R.id.spin_max_size);
	    ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(
	            this, R.array.game_size, android.R.layout.simple_spinner_item);
	    sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinMaxSize.setAdapter(sizeAdapter);
	    
	    // maximale Spieleranzahl in Spinner laden
	    Spinner spinMaxPlayers = (Spinner) findViewById(R.id.spin_max_players);
	    ArrayAdapter<CharSequence> maxPlayersAdapter = ArrayAdapter.createFromResource(
	            this, R.array.game_max_players, android.R.layout.simple_spinner_item);
	    maxPlayersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinMaxPlayers.setAdapter(maxPlayersAdapter);
	    
	    // Spielnamen initialisieren
	    EditText ed_gamename = (EditText) findViewById(R.id.ed_game_name);
	    ed_gamename.setText(new PlayerSQLiteDAO(this).getPlayer().username
	    		+ getResources().getString(R.string.game_name_suggest));
	}
	
	
	public void onButtonClick(View v) {
		switch(v.getId()) {
		case R.id.btn_create_game:
			EditText ed_game_name = (EditText)findViewById(R.id.ed_game_name);
			Spinner spin_max_size = (Spinner)findViewById(R.id.spin_max_size);
			Spinner spin_max_players = (Spinner)findViewById(R.id.spin_max_players);
			EditText ed_game_password = (EditText)findViewById(R.id.ed_game_password);
			
			String spielName = Uri.encode(ed_game_name.getText().toString());
			String maxSize = spin_max_size.getSelectedItem().toString();
			String maxPlayers = spin_max_players.getSelectedItem().toString();
			String password = Uri.encode(ed_game_password.getText().toString());
			
			String user = new PlayerSQLiteDAO(this).getPlayer().username;
			
			HttpGet http_login = new HttpGet(C.URL + "?action=new_game&user=" + user
					+ "&max_players=" + maxPlayers
					+ "&gamename=" + spielName
					+ "&gamesize=" + maxSize
					+ "&password=" + password);
			Intent success = new Intent(this, GameLobby.class);
			
			new HttpAsyncTask(http_login, this, success, true).execute();
		}
	}
}
