package de.cbrn35.apmemory;

public class Game {
	public int id;
	public String name;
	public Player currentPlayer;
	public Player creator;
	public int status;
	public int gameSize;
	public GameField gameField;
	
	public Game(int id, String name, Player creator, int status, int gameSize, GameField gameField) {
		this.id = id;
		this.name = name;
		this.creator = creator;
		this.status = status;
		this.gameSize = gameSize;
		this.gameField = gameField;
	}
}
