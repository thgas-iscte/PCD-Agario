package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import environment.Direction;

public class Server extends Thread {

	public class DealWithClient extends Thread {
		private BufferedReader in;
		private ObjectOutputStream out;
		private HumanPlayer human;

		public DealWithClient(Socket socket) throws IOException {
			doConnections(socket);
		}

		@Override
		public void run() {
			try {
				serve();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void doConnections(Socket socket) throws IOException {
			System.out.println("Doing connections on socket " + socket.toString());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Connections done");
		}

		private void serve() throws IOException {
			try {
				human = new HumanPlayer(Game.NUM_PLAYERS + playerid++, game, HumanPlayer.HUMAN_PLAYER_INITIAL_STRENGTH,
						cdl);
				human.start();

				SendGameStateToPlayer s = new SendGameStateToPlayer();
				s.start();

				while (!game.isGameOver()) {
					String direction;
					direction = in.readLine();
					human.setLastPressedDirection(Direction.valueOf(direction));
				}
				System.out.println("Game is over");
			} finally {
				in.close();
				out.close();
				System.out.println("Server/client connection stopped");
			}
		}

		public class SendGameStateToPlayer extends Thread {

			@Override
			public void run() {
				ServerMessageToClient message;
				while (!game.isGameOver()) {
					try {
						message = new ServerMessageToClient(game);
						out.writeObject(message);

						out.flush();
						out.reset();
						sleep(DELAY);
					} catch (InterruptedException | IOException e) {
						e.printStackTrace();
					}
				}
				//para enviar o estado quando o jogo acabou
				message = new ServerMessageToClient(game);
				try {
					out.writeObject(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public static final int PORTO = 8080;
	private volatile Game game;
	private CountDownLatch cdl;
	public static final int DELAY = 30;
	private int playerid;

	public Server(Game game) {
		this.game = game;
		this.cdl = game.getCountDownLatch();
		playerid = 1;
	}

	@Override
	public void run() {
		ServerSocket ss;
		try {
			ss = new ServerSocket(PORTO);
			try {
				while (true) {
					Socket socket = ss.accept();
					System.out.println("Created server socket " + socket.toString());
					new DealWithClient(socket).start();
				}
			} finally {
				ss.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
