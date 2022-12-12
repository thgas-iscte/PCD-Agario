package game;

import environment.Cell;
import environment.Coordinate;
import environment.Direction;

/**
 * Represents a player.
 * 
 * @author luismota
 *
 */
public abstract class Player extends Thread {

	protected Game game;

	private int id;
	private CountDownLatch cdl;

	private byte currentStrength;
	protected byte originalStrength;

	public static final int MAXSTRENGTH = 10;

	// TODO: get player position from data in game
	public Cell getCurrentCell() {
		for (int i = 0; i < game.DIMX; i++)
			for (int j = 0; j < game.DIMY; j++) {
				Cell cell = game.board[i][j];
				Player p = cell.getPlayer();
				if (p != null && p.equals(this))
					return cell;
			}
		return null;
	}

	public Player(int id, Game game, byte strength, CountDownLatch cdl) {
		super();
		this.id = id;
		this.game = game;
		currentStrength = strength;
		originalStrength = strength;
		this.cdl = cdl;
	}

	public abstract boolean isHumanPlayer();

	@Override
	public abstract void run();

	@Override
	public String toString() {
		return "Player [id=" + id + ", currentStrength=" + currentStrength + ", getCurrentCell()=" + getCurrentCell()
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public byte getCurrentStrength() {
		return currentStrength;
	}

	public int getIdentification() {
		return id;
	}

	
	public void fight(Player otherPlayer) {
		byte newStrength = (byte) Math.min((otherPlayer.currentStrength + this.currentStrength), MAXSTRENGTH);
		if (otherPlayer.getCurrentStrength() > this.getCurrentStrength()) {
			otherPlayer.currentStrength = newStrength;
			if (newStrength == MAXSTRENGTH) {
				cdl.countDown();
				System.out.println(otherPlayer + " chegou ao máximo valor de energia");
			}
			this.currentStrength = 0;
		} else {
			this.currentStrength = newStrength;
			if (newStrength == MAXSTRENGTH) {
				cdl.countDown();
				System.out.println(this + " chegou ao máximo valor de energia");
			}
			otherPlayer.currentStrength = 0;
		}
	}
}
