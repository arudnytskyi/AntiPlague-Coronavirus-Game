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
	private List<Upgrade> upgrades;
	private JPanel mapPanel;
	private JLabel scoreLabel;
	private JLabel timerLabel;
	private int score = 0;
	private int timeElapsed = 0;
	private Timer timer;
	private Timer randomTransportTimer;
	private String difficulty;
	private double infectionRate;

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
		scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
		topPanel.add(scoreLabel);

		timerLabel = new JLabel("Time: 0s");
		timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
		topPanel.add(timerLabel);

		JButton upgradeButton = new JButton("Upgrades");
		upgradeButton.addActionListener(e -> openUpgradeStore());
		topPanel.add(upgradeButton);

		panel.add(topPanel, BorderLayout.NORTH);

		// Map panel
		mapPanel = new JPanel();
		mapPanel.setLayout(null);
		panel.add(mapPanel, BorderLayout.CENTER);

		// Initialize countries and add them to the map
		upgrades = initializeUpgrades();
		countries = initializeCountries(mapPanel);
		transports = initializeTransports(mapPanel);

		// Start
		promptForFirstInfectedCountry();
		startRandomTransport();

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

		// Countries with their coordinates, continent assignments, infection rate, and population
		Object[][] countryData = {
				{"USA", 100, 100, "North America", 0.1, 331000000},
				{"Canada", 200, 50, "North America", 0.08, 38000000},
				{"Mexico", 150, 200, "North America", 0.09, 126000000},
				{"Brazil", 250, 300, "South America", 0.12, 213000000},
				{"UK", 500, 50, "Europe", 0.1, 68000000},
				{"France", 550, 100, "Europe", 0.1, 65000000},
				{"Germany", 600, 150, "Europe", 0.1, 83000000},
				{"India", 700, 300, "Asia", 0.15, 1390000000},
				{"China", 750, 200, "Asia", 0.15, 1440000000},
				{"Australia", 800, 400, "Australia", 0.1, 26000000}
		};

		for (Object[] data : countryData) {
			String name = (String) data[0];
			int x = (int) data[1];
			int y = (int) data[2];
			String continent = (String) data[3];
			double infectionRate = (double) data[4];
			int population = (int) data[5];

			Country country = new Country(name, x, y, continent, infectionRate, population);
			country.addToPanel(mapPanel);
			countryList.add(country);
		}

		return countryList;
	}

	private List<Transport> initializeTransports(JPanel mapPanel) {
		List<Transport> transportList = new ArrayList<>();

		for (int i = 0; i < countries.size(); i++) {
			for (int j = 0; j < countries.size(); j++) {
				if (i == j) continue;

				Country origin = countries.get(i);
				Country destination = countries.get(j);

				// Plain: Allowed between any two countries
				transportList.add(new Transport("Airline", origin, destination, mapPanel));

				// Train: Only if in the same continent
				if (origin.getContinent().equals(destination.getContinent())) {
					transportList.add(new Transport("Train", origin, destination, mapPanel));
				}

				// Ship: Only if in different continents
				if (!origin.getContinent().equals(destination.getContinent())) {
					transportList.add(new Transport("Ship", origin, destination, mapPanel));
				}
			}
		}

		return transportList;
	}

	private void startRandomTransport() {
		randomTransportTimer = new Timer(5000, e -> {
			if (!transports.isEmpty()) {
				Transport randomTransport = transports.get((int) (Math.random() * transports.size()));
				randomTransport.startTransport();
			}
		});
		randomTransportTimer.start();
	}

	private void promptForFirstInfectedCountry() {
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(
					this,
					"Please select the first infected country by clicking on the map.",
					"Initial Infection Selection",
					JOptionPane.INFORMATION_MESSAGE
			);

			// Allow selection for all countries
			for (Country country : countries) {
				country.setSelectable(true);
			}

			// Start a background thread to wait for selection
			new Thread(() -> {
				boolean countrySelected = false;
				while (!countrySelected) {
					for (Country country : countries) {
						if (country.isInfected()) {
							countrySelected = true;
							break;
						}
					}
					try {
						Thread.sleep(100); // Polling delay
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// Once a country is selected, continue the game
				SwingUtilities.invokeLater(() -> {
					for (Country country : countries) {
						country.setSelectable(false); // Disable further selection
					}
					startRandomTransport();
					timer.start();
					JOptionPane.showMessageDialog(
							this,
							"The infection has started! Protect the world from further spread.",
							"Game Start",
							JOptionPane.INFORMATION_MESSAGE
					);
				});
			}).start();
		});
	}

	private void updateTimer() {
		timeElapsed++;
		timerLabel.setText("Time: " + timeElapsed + "s");
		updateGameLogic();
	}

	private void updateGameLogic() {
		for (Country country : countries) {
			if (country.isInfected()) {
				country.updateInfection(); // Dynamically update infection within the country
			}
		}

		// Update the score and check for game over
		score = calculateScore();
		scoreLabel.setText("Score: " + score);

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
		// Stop the main game timer
		if (timer != null) {
			timer.stop();
		}

		// Stop the transport animation timer
		if (randomTransportTimer != null) {
			randomTransportTimer.stop();
		}

		// Stop all animations in transports
		for (Transport transport : transports) {
			transport.stopAnimationManually(); // Implement this in the Transport class
		}

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
