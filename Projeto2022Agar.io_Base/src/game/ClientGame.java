package game;

import java.util.Observable;

public class ClientGame extends Observable {

	private ServerMessageToClient message;

	public ClientGame(ServerMessageToClient message) {
		this.message = message;
	}

	public void setMessage(ServerMessageToClient message) {
		this.message = message;
	}

	public ServerMessageToClient getMessage() {
		return message;
	}

	public void notifyChange() {
		setChanged();
		notifyObservers();
	}

}
