package environment;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import game.Game;
import game.Player;

public class Cell {
	private Coordinate position;
	private Game game;
	private Player player = null;

	private Lock lockCell = new ReentrantLock();
	private Condition positionOccupied = lockCell.newCondition();
	private Condition positionFree = lockCell.newCondition();
//	private Condition inactivePlayer = lockCell.newCondition();

	public Cell(Coordinate position, Game g) {
		super();
		this.position = position;
		this.game = g;
	}

	public Coordinate getPosition() {
		return position;
	}

	public boolean isOcupied() {
		return player != null;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) throws InterruptedException {

		lockCell.lock();
		try {
			// player que ainda nao foi posto em jogo
			while (isOcupied() && player.getCurrentCell() == null) {

				int strength = this.player.getCurrentStrength();
				// jogador que esta na cell esta inativo
				if (strength == 0 || strength == Player.MAXSTRENGTH) {
					game.getRandomCell().setPlayer(player);
					return;
				}

				System.out.println("Cell " + position.x + "-" + position.y + " ocupada pelo jogador "
						+ this.player.getId() + ", jogador " + player.getId() + " bloqueado");
				positionFree.await();
			}

			this.player = player;
			positionOccupied.signalAll();
		} finally {
			lockCell.unlock();
		}
	}

	public void removePlayer(Player player) throws InterruptedException {
		lockCell.lock();
		try {
			this.player = null;
			positionFree.signalAll();
		} finally {
			lockCell.unlock();
		}
	}

	// retorna boolean que indica se é para bloquear jogador
	public synchronized boolean move(Player player, Direction direction) {
		Cell currentCell = this;
		Coordinate newCoordinate = currentCell.getPosition().translate(direction.getVector());
		Cell newCell = game.getCell(newCoordinate);
		if (newCell == null)
			return false;

		lockCells(newCell);

		// caso o jogador tenha morrido enquanto esteve bloqueado, nao se move
		if (player.getCurrentStrength() == 0 || player.getCurrentStrength() == Player.MAXSTRENGTH) {
			unlockCells(newCell);
			return false;
		}
		Player otherPlayer = newCell.getPlayer();
		try {
			if (otherPlayer != null) {
				// se o otherPlayer está inativo
				if (otherPlayer.getCurrentStrength() == 0 || otherPlayer.getCurrentStrength() == Player.MAXSTRENGTH) {

					unlockCells(newCell); // para que o player possa ser comido mesmo enquanto está bloqueado
					return true;
				} else {
					player.fight(otherPlayer);
					wakeUpBlockedPlayers(otherPlayer.getCurrentCell());
				}
			} else {
				newCell.setPlayer(player);
				currentCell.removePlayer(player);
			}
		} catch (InterruptedException e) {
		}
		unlockCells(newCell);
		game.notifyChange();
		return false;
	}

	public synchronized void wakeUpBlockedPlayers(Cell otherCell) {
		positionFree.signalAll();
		otherCell.positionFree.signalAll();
	}

	private void unlockCells(Cell otherCell) {
		otherCell.lockCell.unlock();
		lockCell.unlock();
	}

	private void lockCells(Cell otherCell) {
		otherCell.lockCell.lock();
		lockCell.lock();
	}
}
