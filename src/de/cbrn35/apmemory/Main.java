package de.cbrn35.apmemory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void onButtonClick(View v) {
    	switch(v.getId()) {
    	case R.id.btn_login:
    		Intent i = new Intent(this, Login.class);
    		startActivity(i);
    		break;
    	case R.id.btn_register:
    		Intent r = new Intent(this, Register.class);
    		startActivity(r);
    		break;
    	}
    }
}