package game;

public class CountDownLatch {

	private int numberOfPlayersToFinish;

	public CountDownLatch(int numberOfPlayersToFinish) {
		this.numberOfPlayersToFinish = numberOfPlayersToFinish;
	}

	public synchronized void await() throws InterruptedException{
		while (numberOfPlayersToFinish > 0)
			wait();
	}

	public synchronized void countDown() {
		numberOfPlayersToFinish--;
		if (numberOfPlayersToFinish == 0)
			notifyAll();
	}
	
}
