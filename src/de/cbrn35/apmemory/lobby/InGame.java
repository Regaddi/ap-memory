package de.cbrn35.apmemory.lobby;

import de.cbrn35.apmemory.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
		
		gridview.setNumColumns(4);
		gridview.setAdapter(new ImageAdapter4x4(this));
		
		/*
		gridview.setNumColumns(8);
		gridview.setAdapter(new ImageAdapter8x8(this));
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
		switch(item.getItemId()) {
		case R.id.gamelobby_start:
			Toast.makeText(this, "Noch nicht implementiert!", Toast.LENGTH_LONG).show();
			break;
		case R.id.gamelobby_leave:
			Toast.makeText(this, "Noch nicht implementiert!", Toast.LENGTH_LONG).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
