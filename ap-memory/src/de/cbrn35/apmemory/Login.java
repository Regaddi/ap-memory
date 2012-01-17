package de.cbrn35.apmemory;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import de.cbrn35.apmemory.lobby.Lobby;
import de.cbrn35.apmemory.lobby.PlayerSQLiteDAO;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Login extends Activity {
	private final String savePlayer = "save_player"; 
	public final static String I_GETOPENGAMES = "getOpenGames";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		if(getIntent().hasExtra("data")) {
			Log.i(C.LOGTAG, (getIntent().getExtras().get("data")).toString());
			Player p = null;
			try {
				p = new Player(new JSONObject(getIntent().getExtras().get("data").toString()).getJSONObject("data"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			PlayerSQLiteDAO db = new PlayerSQLiteDAO(this);
			Log.i(C.LOGTAG, p.toString());
			db.persist(p);
			Intent i = new Intent(this, Lobby.class);
			startActivity(i);
		}
	}

	public void onButtonClick(View v) {
		switch(v.getId()) {
		case R.id.btn_login_user:
			EditText ed_user = (EditText)findViewById(R.id.ed_login_user);
			EditText ed_pass = (EditText)findViewById(R.id.ed_password_user);
			
			String user = Uri.encode(ed_user.getText().toString());
			String pass = Uri.encode(ed_pass.getText().toString());
			
			HttpGet http_login = new HttpGet(C.URL + "?action=login&user=" + user + "&pass=" + pass);
			Intent success = new Intent(this, Login.class);
			success.putExtra(savePlayer, 1);
			
			HttpAsyncTask asyncTask = new HttpAsyncTask(http_login, this, success);
			asyncTask.execute();
			break;
		}
	}
}
