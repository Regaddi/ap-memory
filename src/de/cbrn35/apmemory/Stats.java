package de.cbrn35.apmemory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * represents statistics of one player
 */
public class Stats {
	// won games
	public int won;
	// lost games
	public int lost;
	// successful picks (pair found)
	public int pickSuccess;
	// failed picks
	public int pickFail;
	
	public Stats(JSONObject json) {
		try {
			if(json.has("won")) {
				this.won = json.getInt("won");
			}
			if(json.has("lost")) {
				this.lost = json.getInt("lost");
			}
			if(json.has("pickSuccess")) {
				this.pickSuccess = json.getInt("pickSuccess");
			}
			if(json.has("pickFail")) {
				this.pickFail = json.getInt("pickFail");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Stats(int won, int lost, int pickSuccess, int pickFail) {
		this.won = won;
		this.lost = lost;
		this.pickSuccess = pickSuccess;
		this.pickFail = pickFail;
	}
	
	@Override
	public String toString() {
		return "Stats:[won:"+this.won+"; lost:"+this.lost+"; pickSuccess:"+this.pickSuccess+"; pickFail:"+this.pickFail+";]";
	}
}
