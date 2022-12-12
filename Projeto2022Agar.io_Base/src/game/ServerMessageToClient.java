package game;

import java.io.Serializable;
import java.util.ArrayList;

import environment.Coordinate;

public class ServerMessageToClient implements Serializable {

	public final static int DIMY = Game.DIMY;
	public final static int DIMX = Game.DIMX;
	public Coordinate[] coordinatesOfPlayers;
	public int[] strength;
	public boolean[] isHumanPlayer;
	public int[] id;
	public boolean isGameOver;

	public ServerMessageToClient(Game game) {
		this.isGameOver = game.isGameOver();

		ArrayList<Player> players = game.getPlayers();
		int size = players.size();

		coordinatesOfPlayers = new Coordinate[size];
		strength = new int[size];
		isHumanPlayer = new boolean[size];
		id = new int[size];

		for (int i = 0; i < size && players.get(i).getCurrentCell() != null; i++) {
			coordinatesOfPlayers[i] = players.get(i).getCurrentCell().getPosition();
			strength[i] = players.get(i).getCurrentStrength();
			isHumanPlayer[i] = players.get(i).isHumanPlayer();
			id[i] = players.get(i).getIdentification();
		}
	}
}
