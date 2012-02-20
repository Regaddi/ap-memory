package de.cbrn35.apmemory;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class GameField {
	public ArrayList<Card> cards;
	
	public GameField(JSONObject gameField) {
		this.cards = new ArrayList<Card>();
		try {
			for(int i = 0; i < gameField.getJSONArray("cards").length(); i++) {
				JSONObject j = gameField.getJSONArray("cards").getJSONObject(i);
				this.cards.add(
					new Card(
						j.getInt("pos1"),
						j.getInt("pos2"),
						j.getBoolean("visible1"),
						j.getBoolean("visible2"),
						j.getBoolean("paired")
					)
				);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Card findCard(int position) {
		for(Card c : this.cards) {
			if(c.pos1 == position || c.pos2 == position)
				return c;
		}
		return null;
	}
}
