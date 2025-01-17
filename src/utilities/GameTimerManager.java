package utilities;

import java.util.concurrent.*;


public class GameTimerManager {
	private final ScheduledExecutorService scheduler;

	public GameTimerManager() {
		// Initialize the scheduler with a fixed thread pool size
		this.scheduler = Executors.newScheduledThreadPool(5);
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
		return scheduler.scheduleAtFixedRate(task, initialDelay, period, unit);
	}

	public void shutdown() {
		try {
			scheduler.shutdown();
			if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
				scheduler.shutdownNow();
			}
		} catch (InterruptedException e) {
			scheduler.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}
