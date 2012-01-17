package de.cbrn35.apmemory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Player {
	public int id;
	public String username;
	public String email;
	public Date lastActivity;
	public Date signupDate;
	public int currentScore;
	public int currentGameId;
	public Stats stats;
	
	public Player(JSONObject json) {
		Log.i(C.LOGTAG, "parsing Player: "+json.toString());
		try {
			if(json.has("id")) {
				this.id = json.getInt("id");
			}
			if(json.has("username")) {
				this.username = json.getString("username");
			}
			if(json.has("email")) {
				this.email = json.getString("email");
			}
			if(json.has("lastActivity")) {
				Log.i(C.LOGTAG, "parsing lastActivity: "+json.getString("lastActivity"));
				this.lastActivity = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(json.getString("lastActivity"));
			}
			if(json.has("signupDate")) {
				this.signupDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(json.getString("signupDate"));
			}
			if(json.has("currentScore")) {
				this.currentScore = json.getInt("currentScore");
			}
			if(json.has("currentGameId")) {
				this.currentGameId = json.getInt("currentGameId");
			}
			if(json.has("stats")) {
				this.stats = new Stats(json.getJSONObject("stats"));
			}
		} catch(JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Player(int id, String username, String email, Date lastActivity, Date signupDate, int currentScore, int currentGameId, Stats stats) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.lastActivity = lastActivity;
		this.signupDate = signupDate;
		this.currentScore = currentScore;
		this.currentGameId = currentGameId;
		this.stats = stats;
	}
	
	public Player(int id, String username, String email, String lastActivity, String signupDate, int currentScore, int currentGameId, Stats stats) {
		this.id = id;
		this.username = username;
		this.email = email;
		try {
			this.lastActivity = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastActivity);
			this.signupDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(signupDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.currentScore = currentScore;
		this.currentGameId = currentGameId;
		this.stats = stats;
	}
	
	public String toString() {
		return "Player:["
				+"id:"+this.id+"; "
				+"username:"+this.username+"; "
				+"email:"+this.email+"; "
				+"lastActivity:"+this.lastActivity.toString()+"; "
				+"signupDate:"+this.signupDate.toString()+"; "
				+"currentScore:"+this.currentScore+"; "
				+"currentGameId:"+this.currentGameId+"; "
				+"Stats:"+this.stats+";]";
	}
}
