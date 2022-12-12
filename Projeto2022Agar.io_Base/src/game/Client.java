package game;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import gui.ClientGameGUI;

public class Client {

	private ObjectInputStream in;
	private PrintWriter out;
	private Socket socket;
	private String address;
	private int port;
	private ClientGameGUI clientGUI;
	private ClientGame clientGame;

	public static void main(String[] args) {
		new Client("127.0.0.1", Server.PORTO, false).runClient();
	}

	public Client(String address, int port, boolean alternativeKeys) {
		this.address = address;
		this.port = port;

		clientGUI = new ClientGameGUI(alternativeKeys);
		clientGame = clientGUI.getClientGame();
		clientGUI.init();
	}

	public void runClient() {
		try {
			connectToServer();
			interactWithServer();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				System.out.println("Client socket closed");
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	private void connectToServer() throws IOException {
		InetAddress inetAddress = InetAddress.getByName(address);
		System.out.println("Endereco:" + inetAddress);
		socket = new Socket(inetAddress, port);
		System.out.println("Socket:" + socket);
		in = new ObjectInputStream(socket.getInputStream());
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	}

	private void interactWithServer() throws ClassNotFoundException, IOException {
		while (true) {
			ServerMessageToClient message = (ServerMessageToClient) in.readObject();

//			System.out.println("Num players recebido: " + message.coordinatesOfPlayers.length);
			
			clientGame.setMessage(message);
			clientGame.notifyChange();

			if (message.isGameOver)
				break;

			if (clientGUI.boardGui.getLastPressedDirection() != null) {
				String str = clientGUI.boardGui.getLastPressedDirection().toString();
				out.println(str);
				clientGUI.boardGui.clearLastPressedDirection();
				
				System.out.println("Direction sent to server: " + str);
			}
		}
	}
}
