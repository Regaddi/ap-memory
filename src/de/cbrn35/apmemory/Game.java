package de.cbrn35.apmemory;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Game {
	public int id;
	public String name;
	public Player currentPlayer;
	public Player creator;
	public int status;
	public boolean isPrivate;
	public int maxPlayers;
	public int gameSize;
	public GameField gameField;
	
	public Game(int id, String name, Player creator, int status, boolean isPrivate, int maxPlayers, int gameSize, GameField gameField) {
		this.id = id;
		this.name = name;
		this.creator = creator;
		this.status = status;
		this.isPrivate = isPrivate;
		this.maxPlayers = maxPlayers;
		this.gameSize = gameSize;
		this.gameField = gameField;
	}
	
	public Game(JSONObject j) {
		try {
			if(j.has("id")) {
				this.id = j.getInt("id");
			}
			if(j.has("name")) {
				this.name = j.getString("name");
			}
			if(j.has("currentPlayer")) {
				if(j.getString("currentPlayer").equals("null")) {
					this.currentPlayer = null;
				} else {
					this.currentPlayer = new Player(j.getJSONObject("currentPlayer"));
				}
			}
			if(j.has("creator")) {
				this.creator = new Player(j.getJSONObject("creator"));
			}
			if(j.has("status")) {
				this.status = j.getInt("status");
			}
			if(j.has("isPrivate")) {
				this.isPrivate = j.getBoolean("isPrivate");
			}
			if(j.has("maxPlayers")) {
				this.maxPlayers = j.getInt("maxPlayers");
			}
			if(j.has("gameSize")) {
				this.gameSize = j.getInt("gameSize");
			}
			if(j.has("gameField")) {
				this.gameField = new GameField(j.getJSONObject("gameField"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i(C.LOGTAG, this.toString());
	}
	
	@Override
	public String toString() {
		return "Game:[id:"+this.id+"; name:"+this.name+"; creator:"+this.creator.toString()+"; isPrivate:"+this.isPrivate+"; maxPlayers: "+this.maxPlayers+"]";
	}
}
