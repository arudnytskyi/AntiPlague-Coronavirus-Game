package utilities;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {
	private static final String FILE_NAME = "highscores.dat";
	private List<HighScore> highScores;

	public HighScoreManager() {
		highScores = loadHighScores();
	}

	// Add a new high score
	public void addHighScore(String playerName, int score) {
		highScores.add(new HighScore(playerName, score));
		Collections.sort(highScores); // Sort by score descending
		if (highScores.size() > 10) {
			highScores = highScores.subList(0, 10); // Keep top 10
		}
		saveHighScores();
	}

	// Get the high scores list
	public List<HighScore> getHighScores() {
		return highScores;
	}

	// Save high scores to file
	private void saveHighScores() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
			oos.writeObject(highScores);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Load high scores from file
	@SuppressWarnings("unchecked")
	private List<HighScore> loadHighScores() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
			return (List<HighScore>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			return new ArrayList<>(); // Return empty list if file doesn't exist
		}
	}

	// Inner class to represent a high score
	public static class HighScore implements Serializable, Comparable<HighScore> {
		private String playerName;
		private int score;

		public HighScore(String playerName, int score) {
			this.playerName = playerName;
			this.score = score;
		}

		public String getPlayerName() {
			return playerName;
		}

		public int getScore() {
			return score;
		}

		@Override
		public int compareTo(HighScore other) {
			return Integer.compare(other.score, this.score); // Descending order
		}

		@Override
		public String toString() {
			return playerName + " - " + score;
		}
	}
}
