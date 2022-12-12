package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

import environment.Cell;
import environment.Coordinate;

public class Game extends Observable implements Serializable {

	public static final int DIMY = 30;
	public static final int DIMX = 30;
	public static final int NUM_PLAYERS = 30;
	private static final int NUM_FINISHED_PLAYERS_TO_END_GAME = 3;

	public static final long REFRESH_INTERVAL = 400;
	public static final double MAX_INITIAL_STRENGTH = 3;
	public static final long MAX_WAITING_TIME_FOR_MOVE = 2000;
	public static final long INITIAL_WAITING_TIME = 10000;

	protected Cell[][] board;
	private ArrayList<Player> players;
	private boolean gameOver;
	public CountDownLatch cdl;

	public Game() {
		board = new Cell[Game.DIMX][Game.DIMY];

		for (int x = 0; x < Game.DIMX; x++)
			for (int y = 0; y < Game.DIMY; y++)
				board[x][y] = new Cell(new Coordinate(x, y), this);

		cdl = new CountDownLatch(NUM_FINISHED_PLAYERS_TO_END_GAME);
		players = new ArrayList<Player>();
		gameOver = false;
	}

	/**
	 * @param player
	 * @throws InterruptedException
	 */

	public void addPlayerToGame(Player player) throws InterruptedException {
		Cell initialPos = getRandomCell();
//		Cell initialPos = board[0][0];
		initialPos.setPlayer(player);
		players.add(player);
		// To update GUI
		notifyChange();
	}

	public Cell getCell(Coordinate at) {
		if (at.x < 0 || at.y < 0 || at.x >= DIMX || at.y >= DIMY)
			return null;
		return board[at.x][at.y];
	}

	/**
	 * Updates GUI. Should be called anytime the game state changes
	 */
	public void notifyChange() {
		setChanged();
		notifyObservers();
	}

	public Cell getRandomCell() {
		Cell newCell = getCell(new Coordinate((int) (Math.random() * Game.DIMX), (int) (Math.random() * Game.DIMY)));
		return newCell;
	}

	public void start() {

		for (int i = 0; i < NUM_PLAYERS; i++) {
			BotPlayer bot = new BotPlayer(i, this, /* (byte) 5 */ (byte) (Math.random() * MAX_INITIAL_STRENGTH + 1),
					cdl);
			bot.start();
		}

		try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		interruptAllPlayers();
		gameOver = true;
	}

	private synchronized void interruptAllPlayers() {
		for (Player p : players)
			p.interrupt();
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public CountDownLatch getCountDownLatch() {
		return cdl;
	}
}
