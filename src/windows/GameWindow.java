package windows;

import utilities.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class GameWindow extends JFrame {
	private List<Country> countries;
	private List<Transport> transports;
	private JPanel mapPanel;
	private JLabel scoreLabel;
	private JLabel timerLabel;
	private int score = 0;
	private int timeElapsed = 0;
	private Timer timer;
	private String difficulty;
	private double infectionRate;
	private List<Upgrade> upgrades; // Declare the upgrades variable

	public GameWindow(String difficulty) {
		this.difficulty = difficulty;

		// Set infection rate based on difficulty
		switch (difficulty) {
			case "Easy":
				infectionRate = 0.05;
				break;
			case "Medium":
				infectionRate = 0.1;
				break;
			case "Hard":
				infectionRate = 0.2;
				break;
		}

		// Initialize the upgrades list
		upgrades = initializeUpgrades();

		// Set up the frame
		setTitle("AntiPlague Game - " + difficulty + " Mode");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
		setResizable(false);

		// Main panel
		JPanel panel = new JPanel(new BorderLayout());

		// Top panel for score and timer
		JPanel topPanel = new JPanel(new GridLayout(1, 3));
		scoreLabel = new JLabel("Score: 0");
		timerLabel = new JLabel("Time: 0s");
		JButton upgradeButton = new JButton("Upgrades");
		upgradeButton.addActionListener(e -> openUpgradeStore());
		scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
		timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
		topPanel.add(scoreLabel);
		topPanel.add(timerLabel);
		topPanel.add(upgradeButton);
		panel.add(topPanel, BorderLayout.NORTH);

		// Map panel
		mapPanel = new JPanel();
		mapPanel.setLayout(null);
		panel.add(mapPanel, BorderLayout.CENTER);

		// Initialize countries and add them to the map
		countries = initializeCountries(mapPanel);
		transports = initializeTransports(mapPanel);

		// Start transport animations
		startTransportAnimations();

		// Control panel with pause and quit buttons
		JPanel controlPanel = new JPanel();
		JButton pauseButton = new JButton("Pause");
		JButton quitButton = new JButton("Quit");
		controlPanel.add(pauseButton);
		controlPanel.add(quitButton);
		panel.add(controlPanel, BorderLayout.SOUTH);

		// Add panel to frame
		add(panel);

		// Timer for game logic
		timer = new Timer(1000, e -> updateTimer());
		timer.start();

		// Button actions
		pauseButton.addActionListener(e -> pauseGame());
		quitButton.addActionListener(e -> quitGame());

		// Add Ctrl+Shift+Q shortcut
		addKeyBindings(panel);

		// Make frame visible
		setVisible(true);
	}

	private List<Upgrade> initializeUpgrades() {
		List<Upgrade> upgradeList = new ArrayList<>();
		upgradeList.add(new Upgrade("Quarantine", 50, "Reduce infection rate in one country.", () -> {
			infectionRate -= 0.01; // Reduce infection rate globally
			JOptionPane.showMessageDialog(this, "Infection rate reduced globally!");
		}));
		upgradeList.add(new Upgrade("Vaccination", 100, "Prevent infection in one country.", () -> {
			for (Country country : countries) {
				if (!country.isInfected()) {
					JOptionPane.showMessageDialog(this, country.getName() + " vaccinated!");
					break;
				}
			}
		}));
		return upgradeList;
	}

	private void openUpgradeStore() {
		UpgradeStoreDialog store = new UpgradeStoreDialog(this, upgrades, score);
		store.setVisible(true);
		score = store.getRemainingPoints();
		scoreLabel.setText("Score: " + score);
	}

	private List<Country> initializeCountries(JPanel mapPanel) {
		List<Country> countryList = new ArrayList<>();
		String[] countryNames = {"USA", "Canada", "Mexico", "Brazil", "UK", "France", "Germany", "India", "China", "Australia"};
		int[][] positions = {
				{100, 100}, {200, 50}, {150, 200}, {250, 300}, {500, 50},
				{550, 100}, {600, 150}, {700, 300}, {750, 200}, {800, 400}
		};

		for (int i = 0; i < countryNames.length; i++) {
			Country country = new Country(countryNames[i], positions[i][0], positions[i][1], 0.1);
			country.addToPanel(mapPanel);
			countryList.add(country);
		}

		return countryList;
	}

	private List<Transport> initializeTransports(JPanel mapPanel) {
		List<Transport> transportList = new ArrayList<>();

		// Create transport connections
		transportList.add(new Transport("Airline", countries.get(0), countries.get(4), mapPanel)); // USA to UK
		transportList.add(new Transport("Ship", countries.get(3), countries.get(7), mapPanel));   // Brazil to India
		transportList.add(new Transport("Train", countries.get(1), countries.get(2), mapPanel));  // Canada to Mexico

		return transportList;
	}

	private void startTransportAnimations() {
		for (Transport transport : transports) {
			transport.startTransport();
		}
	}

	private void updateTimer() {
		timeElapsed++;
		timerLabel.setText("Time: " + timeElapsed + "s");
		updateGameLogic();
	}

	private void updateGameLogic() {
		for (Country country : countries) {
			country.updateInfection();
		}

		// Update the score based on the number of uninfected countries
		score = calculateScore();
		scoreLabel.setText("Score: " + score);

		// Check if the game is over
		if (isGameOver()) {
			endGame();
		}
	}

	private int calculateScore() {
		int uninfectedCount = 0;
		for (Country country : countries) {
			if (!country.isInfected()) {
				uninfectedCount++;
			}
		}
		return uninfectedCount * 10; // 10 points per uninfected country
	}

	private boolean isGameOver() {
		for (Country country : countries) {
			if (!country.isInfected()) {
				return false; // At least one country is still safe
			}
		}
		return true; // All countries are infected
	}

	private void endGame() {
		timer.stop();
		JOptionPane.showMessageDialog(this, "Game Over! Your Score: " + score, "Game Over", JOptionPane.INFORMATION_MESSAGE);
		dispose();
	}

	private void pauseGame() {
		timer.stop();
		JOptionPane.showMessageDialog(this, "Game Paused. Press OK to Resume.", "Pause", JOptionPane.INFORMATION_MESSAGE);
		timer.start();
	}

	private void quitGame() {
		timer.stop();
		int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Quit Game", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			dispose();
		} else {
			timer.start();
		}
	}

	private void addKeyBindings(JPanel panel) {
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "quitToMenu");
		panel.getActionMap().put("quitToMenu", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				quitGame();
			}
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new GameWindow("Medium")); // Test with medium difficulty
	}
}
