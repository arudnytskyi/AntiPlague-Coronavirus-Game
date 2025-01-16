package utilities;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {
	private static final String FILE_NAME = "highscores.dat";
	private static HighScoreManager instance;
	private List<HighScore> highScores;

	public HighScoreManager() {
		highScores = loadHighScores();
	}

	public void addHighScore(String playerName, int score, String difficultyLevel) {
		double multiplier = switch (difficultyLevel) {
			case "Easy" -> 1;
			case "Medium" -> 1.1;
			case "Hard" -> 1.2;
			default -> 0;
		};
		int finalScore = (int) (score * multiplier);

		highScores.add(new HighScore(playerName, finalScore));
		Collections.sort(highScores);

		if (highScores.size() > 10) {
			highScores = new ArrayList<>(highScores.subList(0, 10));
		}

		saveHighScores();
	}

	public List<HighScore> getHighScores() {
		return new ArrayList<>(highScores);
	}

	private void saveHighScores() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
			oos.writeObject(highScores);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static HighScoreManager getInstance() {
		if (instance == null) {
			synchronized (HighScoreManager.class) {
				if (instance == null) {
					instance = new HighScoreManager();
				}
			}
		}
		return instance;
	}

	private List<HighScore> loadHighScores() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
			return (List<HighScore>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			return new ArrayList<>();
		}
	}

	public static class HighScore implements Serializable, Comparable<HighScore> {
		private final String playerName;
		private final int score;

		public HighScore(String playerName, int score) {
			this.playerName = playerName;
			this.score = score;
		}

		@Override
		public int compareTo(HighScore other) {
			return Integer.compare(other.score, this.score);
		}

		@Override
		public String toString() {
			return playerName + " - " + score;
		}
	}
}
