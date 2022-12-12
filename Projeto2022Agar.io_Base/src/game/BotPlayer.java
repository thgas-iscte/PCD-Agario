package game;

import environment.Cell;
import environment.Coordinate;
import environment.Direction;

public class BotPlayer extends Player {

	public BotPlayer(int id, Game game, byte strength, CountDownLatch cdl) {
		super(id, game, strength, cdl);
	}

	@Override
	public boolean isHumanPlayer() {
		return false;
	}

	@Override
	public void run() {
		try {
			sleep(game.INITIAL_WAITING_TIME);
			game.addPlayerToGame(this);
			
			while (getCurrentStrength() != Player.MAXSTRENGTH && getCurrentStrength() != 0 && !game.isGameOver()) {
				int randMove = (int) (Math.random() * 4);
				Cell currentCell = getCurrentCell();
				boolean blocked = false;
				switch (randMove) {
				case 0:
					blocked = currentCell.move(this, Direction.UP);
					if (blocked)
						sleep(Game.MAX_WAITING_TIME_FOR_MOVE);

					break;
				case 1:
					blocked = currentCell.move(this, Direction.DOWN);
					if (blocked)
						sleep(Game.MAX_WAITING_TIME_FOR_MOVE);

					break;
				case 2:
					blocked = currentCell.move(this, Direction.LEFT);
					if (blocked)
						sleep(Game.MAX_WAITING_TIME_FOR_MOVE);

					break;
				case 3:
					blocked = currentCell.move(this, Direction.RIGHT);
					if (blocked)
						sleep(Game.MAX_WAITING_TIME_FOR_MOVE);
					break;
				}
				sleep(Game.REFRESH_INTERVAL * this.originalStrength);
			}

		} catch (InterruptedException e) {
			return;
		}
	}
}
