package de.cbrn35.apmemory;

public class Card {
	public int pos1;
	public int pos2;
	public boolean visible1;
	public boolean visible2;
	public boolean paired;
	
	public Card(int pos1, int pos2, boolean vis1, boolean vis2, boolean paired) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.visible1 = vis1;
		this.visible2 = vis2;
		this.paired = paired;
	}
	
	public String toString() {
		return "Card:[pos1: "+pos1+"; pos2: "+pos2+"; vis1: "+visible1+"; vis2: "+visible2+"; paired: "+paired+"]";
	}
}
