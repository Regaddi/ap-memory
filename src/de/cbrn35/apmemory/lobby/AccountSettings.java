package de.cbrn35.apmemory.lobby;

import java.util.concurrent.ExecutionException;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import de.cbrn35.apmemory.C;
import de.cbrn35.apmemory.HttpAsyncTask;
import de.cbrn35.apmemory.Player;
import de.cbrn35.apmemory.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * represents account settings form
 */
public class AccountSettings extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_settings);
		
		// set email from local saved player object
		EditText email = (EditText)findViewById(R.id.account_email);
		Player p = new PlayerSQLiteDAO(this).getPlayer();
		email.setText(p.email);
	}
	
	public void onButtonClick(View v) {
		switch(v.getId()) {
		case R.id.account_save_bt:
			// get entered data
			EditText email = (EditText)findViewById(R.id.account_email);
			EditText oldpw = (EditText)findViewById(R.id.account_oldpw);
			EditText newpw = (EditText)findViewById(R.id.account_newpw);
			EditText newpw2 = (EditText)findViewById(R.id.account_newpw2);
			
			Player p = new PlayerSQLiteDAO(this).getPlayer();
			
			if(newpw.getText().toString().length() < 6) {
				// password too short
				newpw.setError("Passwort muss mindestens 6 Zeichen lang sein!");
				return;
			}
			
			if(newpw.getText().toString().equals(newpw2.getText().toString())) {
				// passwords match
				// perform action on server
				HttpGet get = new HttpGet(C.URL+"?action=set_account_settings"
						+"&user="+p.username
						+"&email="+email.getText().toString()
						+"&oldpw="+oldpw.getText().toString()
						+"&newpw="+newpw.getText().toString()
						+"&newpw2="+newpw2.getText().toString());
				HttpAsyncTask async = new HttpAsyncTask(get, this, null, true);
				async.execute();
				
				try {
					JSONObject result = async.get();
					if(result.getInt("error") == 0) {
						// reset form on success
						Toast.makeText(this, result.getString("response"), Toast.LENGTH_LONG).show();
						oldpw.setText("");
						newpw.setText("");
						newpw2.setText("");
					} else {
						Toast.makeText(this, result.getString("error_msg"), Toast.LENGTH_LONG).show();
					}
				} catch (InterruptedException e) {
					Toast.makeText(this, getResources().getString(R.string.err_no_connection), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				newpw2.setError("Bitte geben Sie zweimal das gleiche Passwort ein!");
			}
		}
	}
}
