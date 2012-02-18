package de.cbrn35.apmemory;

import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity {
	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
			"[a-zA-Z0-9+._%-+]{1,256}" +
			"@" +
			"[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
			"(" +
			"." +
			"[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
			")+"
			);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
	}
	
	public void onButtonClick(View v) {
		switch(v.getId()) {
		case R.id.btn_do_register:
			EditText ed_username = (EditText)findViewById(R.id.ed_register_username);
			EditText ed_email = (EditText)findViewById(R.id.ed_register_email);
			EditText ed_pass = (EditText)findViewById(R.id.ed_register_pass);
			EditText ed_pass_copy = (EditText)findViewById(R.id.ed_register_pass_copy);
			
			String username = ed_username.getText().toString();
			String email = ed_email.getText().toString();
			String pass = ed_pass.getText().toString();
			String pass_copy = ed_pass_copy.getText().toString();
			
			Log.i(C.LOGTAG, username+" "+email+" "+pass+" "+pass_copy);
			
			if(username.trim().equals("")) {
				Toast.makeText(this, this.getResources().getString(R.string.err_register_empty_username), Toast.LENGTH_LONG).show();
			}
			else if(email.trim().equals("")) {
				Toast.makeText(this, this.getResources().getString(R.string.err_register_empty_email), Toast.LENGTH_LONG).show();
			}
			else if(!checkEmail(email)) {
				Toast.makeText(this, this.getResources().getString(R.string.err_register_email_invalid), Toast.LENGTH_LONG).show();
			}
			else if(pass.trim().equals("") || pass_copy.trim().equals("")) {
				Toast.makeText(this, this.getResources().getString(R.string.err_register_empty_pass), Toast.LENGTH_LONG).show();
			}
			else if(!pass.equals(pass_copy)) {
				Toast.makeText(this, this.getResources().getString(R.string.err_register_passes_different), Toast.LENGTH_LONG).show();
			}
			else {
				HttpGet get = new HttpGet(C.URL + "?action=register&user="+username+"&pass="+pass+"&email="+email);
				Intent success = new Intent(this, Main.class);
				new HttpAsyncTask(get, this, success, true).execute();
			}
			break;
		}
	}
	
	private boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}
}
