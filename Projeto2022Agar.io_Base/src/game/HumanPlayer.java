package game;

import environment.Cell;
import environment.Direction;

public class HumanPlayer extends Player {

	public static final byte HUMAN_PLAYER_INITIAL_STRENGTH = 5;
	private volatile Direction lastPressedDirection;

	public HumanPlayer(int id, Game game, byte strength, CountDownLatch cdl) {
		super(id, game, strength, cdl);
		lastPressedDirection = null;
	}

	@Override
	public boolean isHumanPlayer() {
		return true;
	}

	@Override
	public void run() {
		try {
			game.addPlayerToGame(this);

			while (getCurrentStrength() != Player.MAXSTRENGTH && getCurrentStrength() != 0) {
				
				if (getLastPressedDirection() != null) {
					Cell currentCell = getCurrentCell();
					currentCell.move(this, getLastPressedDirection());
					setDirectionNull();
				}
			}
		} catch (InterruptedException e) {
			return;
		}

	}

	public void setLastPressedDirection(Direction direction) {
		lastPressedDirection = direction;
	}

	public Direction getLastPressedDirection() {
		return lastPressedDirection;
	}

	public void setDirectionNull() {
		lastPressedDirection = null;
	}

	public static void main(String[] args) {
		Direction dir = null;
		System.out.println(dir != null);
	}
}
