package utilities;

public class GameTimerManager {
	private Thread taskThread;
	private boolean isRunning;

	public GameTimerManager() {
		isRunning = true;
	}

	public Thread scheduleAtFixedRate(Runnable task, long initialDelay, long periodMillis) {
		taskThread = new Thread(() -> {
			try {
				Thread.sleep(initialDelay);
				while (isRunning) {
					task.run();
					Thread.sleep(periodMillis);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
		taskThread.start();
		return taskThread;
	}

	public void shutdown() {
		isRunning = false;
		if (taskThread != null && taskThread.isAlive()) {
			taskThread.interrupt();
		}
	}
}
