package de.cbrn35.apmemory.lobby;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "player.sqlite";
	
	public MySQLiteOpenHelper(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + PlayerDB.TABLE_NAME + " ("
				+ PlayerDB.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ PlayerDB.USERNAME + " TEXT NOT NULL,"
				+ PlayerDB.EMAIL + " TEXT NOT NULL,"
				+ PlayerDB.LASTACTIVITY + " TEXT NOT NULL,"
				+ PlayerDB.SIGNUPTIMESTAMP + " TEXT NOT NULL,"
				+ PlayerDB.CURRENTSCORE + " INTEGER NOT NULL,"
				+ PlayerDB.CURRENTGAMEID + " INTEGER NOT NULL,"
				+ PlayerDB.WON + " INTEGER NOT NULL,"
				+ PlayerDB.LOST + " INTEGER NOT NULL,"
				+ PlayerDB.PICKSUCCESS + " INTEGER NOT NULL,"
				+ PlayerDB.PICKFAIL + " INTEGER NOT NULL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		switch(newVersion) {
		}
	}

}
