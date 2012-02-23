package de.cbrn35.apmemory.lobby;

import java.util.Date;

import de.cbrn35.apmemory.Player;
import android.content.ContentValues;

/**
 * 
 * creates and manages the player table
 *
 */
public final class PlayerDB {
	public static String TABLE_NAME = "player";
	public static String ID = "_id";
	public static String USERNAME = "username";
	public static String EMAIL = "email";
	public static String LASTACTIVITY = "last_activity";
	public static String SIGNUPTIMESTAMP = "signup_timestamp";
	public static String CURRENTSCORE = "current_score";
	public static String CURRENTGAMEID = "current_game_id";
	
	public static String WON = "won";
	public static String LOST = "lost";
	public static String PICKSUCCESS = "pick_success";
	public static String PICKFAIL = "pick_fail";
	
	private PlayerDB() {}
	
	public static ContentValues createContentValues(Player player) {
		ContentValues cv = new ContentValues();
		cv.put(ID, player.id);
		cv.put(USERNAME, player.username);
		cv.put(EMAIL, player.email);
		if(player.lastActivity != null) {
			Date d = player.lastActivity;
			int year = 1900+d.getYear();
			int month = d.getMonth()+1;
			int day = d.getDate();
			int hours = d.getHours();
			int mins = d.getMinutes();
			int secs = d.getSeconds();
			String lAString = year+"-"+
					(month < 10 ? "0"+month : month)+"-"+
					(day < 10 ? "0"+day : day)+" "+
					(hours < 10 ? "0"+hours : hours)+":"+
					(mins < 10 ? "0"+mins : mins)+":"+
					(secs < 10 ? "0"+secs : secs);
			cv.put(LASTACTIVITY, lAString);
		} else {
			cv.put(LASTACTIVITY, "0000-00-00 00:00:00");
		}
		if(player.signupDate != null) {
			Date d = player.signupDate;
			int year = 1900+d.getYear();
			int month = d.getMonth()+1;
			int day = d.getDate();
			int hours = d.getHours();
			int mins = d.getMinutes();
			int secs = d.getSeconds();
			String sDString = year+"-"+
					(month < 10 ? "0"+month : month)+"-"+
					(day < 10 ? "0"+day : day)+" "+
					(hours < 10 ? "0"+hours : hours)+":"+
					(mins < 10 ? "0"+mins : mins)+":"+
					(secs < 10 ? "0"+secs : secs);
			cv.put(SIGNUPTIMESTAMP, sDString);
		} else {
			cv.put(SIGNUPTIMESTAMP, "0000-00-00 00:00:00");
		}
		cv.put(CURRENTSCORE, player.currentScore);
		cv.put(CURRENTGAMEID, player.currentGameId);
		cv.put(WON, player.stats.won);
		cv.put(LOST, player.stats.lost);
		cv.put(PICKSUCCESS, player.stats.pickSuccess);
		cv.put(PICKFAIL, player.stats.pickFail);
		return cv;
	}
}
