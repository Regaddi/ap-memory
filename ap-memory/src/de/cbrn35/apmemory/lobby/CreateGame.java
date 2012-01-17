package de.cbrn35.apmemory.lobby;

import org.apache.http.client.methods.HttpGet;

import de.cbrn35.apmemory.C;
import de.cbrn35.apmemory.HttpAsyncTask;
import de.cbrn35.apmemory.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateGame extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.creategame);
	    Spinner s = (Spinner) findViewById(R.id.spin_max_size);
	    ArrayAdapter adapter = ArrayAdapter.createFromResource(
	            this, R.array.spiel_groesse, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    s.setAdapter(adapter);
	}
	
	
	public void onButtonClick(View v) {
		switch(v.getId()) {
		case R.id.btn_create_game:
			EditText ed_spiel_name = (EditText)findViewById(R.id.ed_spiel_name);
			Spinner spin_max_size = (Spinner)findViewById(R.id.spin_max_size);
			
			String spielName = Uri.encode(ed_spiel_name.getText().toString());
			String maxSize = spin_max_size.getSelectedItem().toString();
			
			String user = new PlayerSQLiteDAO(this).getPlayer().username;
			
			HttpGet http_login = new HttpGet(C.URL + "?action=new_game&user=" + user + "&gamename=" + spielName + "&gamesize=" + maxSize);
			Intent success = new Intent(this, Lobby.class);
			
			new HttpAsyncTask(http_login, this, success).execute();
		}
	}
}
