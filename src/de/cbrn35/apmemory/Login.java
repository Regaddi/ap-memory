package de.cbrn35.apmemory;

import java.util.concurrent.ExecutionException;

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

/**
 * represents login activity
 */
public class Login extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	}

	public void onButtonClick(View v) {
		switch(v.getId()) {
		case R.id.btn_login_user:
			// get user data
			EditText ed_user = (EditText)findViewById(R.id.ed_login_user);
			EditText ed_pass = (EditText)findViewById(R.id.ed_password_user);
			
			String user = Uri.encode(ed_user.getText().toString());
			String pass = Uri.encode(ed_pass.getText().toString());
			
			// perform login-action on server
			HttpGet http_login = new HttpGet(C.URL + "?action=login&user=" + user + "&pass=" + pass);
			
			HttpAsyncTask asyncTask = new HttpAsyncTask(http_login, this, null, true);
			asyncTask.execute();
			
			try {
				// get response JSONObject
				JSONObject result = asyncTask.get();
				Log.i(C.LOGTAG, result.toString());
				Player p = null;
				// update local player object
				p = new Player(result.getJSONObject("data"));
				PlayerSQLiteDAO db = new PlayerSQLiteDAO(this);
				db.persist(p);
				// go to lobby
				Intent i = new Intent(this, Lobby.class);
				startActivity(i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
	}
}
