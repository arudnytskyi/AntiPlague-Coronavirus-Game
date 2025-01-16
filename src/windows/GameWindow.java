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
	private List<JButton> activeIcons;
	private JPanel mapPanel;
	private JLayeredPane layeredPane;
	private JLabel scoreLabel;
	private JLabel timerLabel;
	private JProgressBar vaccineProgressBar;
	private int score = 0;
	private int points = 0;
	private int timeElapsed = 0;
	private Timer timer;
	private Timer randomTransportTimer;
	private Timer labTimer;
	private Timer infectionRateTimer;
	private Timer globalAwarenessTimer;
	private Timer iconSpawnerTimer;
	private String difficulty;
	private double infectionRate;
	private static int globalAwareness = 0;
	private int laboratoryCount = 0;
	private boolean isInfectionStarted = false;
	HighScoreManager highScoreManager;

	public GameWindow(String difficulty) {
		this.difficulty = difficulty;

		// Set infection rate based on difficulty
		switch (difficulty) {
			case "Easy":
				infectionRate = 1;
				break;
			case "Medium":
				infectionRate = 1.1;
				break;
			case "Hard":
				infectionRate = 1.2;
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
		JPanel topPanel = new JPanel(new GridLayout(1, 4));

		scoreLabel = new JLabel("Score: 0");
		scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
		topPanel.add(scoreLabel);

		timerLabel = new JLabel("Time: 0s");
		timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
		topPanel.add(timerLabel);

		vaccineProgressBar = new JProgressBar(0, 100); // Progress from 0 to 100
		vaccineProgressBar.setValue(0); // Initial value
		vaccineProgressBar.setStringPainted(true);
		vaccineProgressBar.setFont(new Font("Arial", Font.BOLD, 12));
		topPanel.add(vaccineProgressBar);

		JButton upgradeButton = new JButton("Upgrades");
		upgradeButton.addActionListener(e -> openUpgradeStore());
		topPanel.add(upgradeButton);

		panel.add(topPanel, BorderLayout.NORTH);

		// Map panel with layered pane
		mapPanel = new JPanel(null);
		layeredPane = new JLayeredPane();
		layeredPane.setLayout(null);
		layeredPane.setBounds(0, 0, 800, 500);
		mapPanel.add(layeredPane);
		panel.add(mapPanel, BorderLayout.CENTER);

		// Initialize countries and add them to the map
		upgrades = initializeUpgrades();
		countries = initializeCountries(mapPanel);
		transports = initializeTransports(mapPanel);

		activeIcons = new ArrayList<>();

		// Start
		setupMainTimer();
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

		globalAwarenessTimer = new Timer(5000, e -> updateGlobalAwareness()); // Update every 5 seconds
		globalAwarenessTimer.start();

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

		upgradeList.add(new Upgrade("Vaccine Research", 1, "Adds +5% to vaccine development.", () -> {
			int currentProgress = vaccineProgressBar.getValue();
			vaccineProgressBar.setValue(Math.min(currentProgress + 5, 100));
			JOptionPane.showMessageDialog(this, "Vaccine research progressed by +5%.");
		}));

		upgradeList.add(new Upgrade("Build Laboratory", 1, "Adds a laboratory that increases vaccine progress over time.", () -> {
			startLaboratoryProgress();
			JOptionPane.showMessageDialog(this, "Laboratory built! Vaccine progress will now increase over time.");
		}));

		upgradeList.add(new Upgrade("Vaccine Distribution", 1, "Enable vaccine distribution via transport.", () -> {
			startVaccineTransport();
			JOptionPane.showMessageDialog(this, "Vaccine distribution via transport enabled.");
		}));

		upgradeList.add(new Upgrade("Cancel Mutation", 1, "Decreases the infection rate by 0.01 (1%).", () -> {
			if (infectionRate > 0.9) {
				infectionRate -= 0.01;
				JOptionPane.showMessageDialog(this, "Mutation canceled! Infection rate decreased by 1%.",
						"Upgrade Successful", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Infection rate is already at the minimum!",
						"Upgrade Failed", JOptionPane.WARNING_MESSAGE);
			}
		}));

		upgradeList.add(new Upgrade("Sanitation Protocols", 1, "Reduce infection spread during transport by 50%.", () -> {
			Transport.setSanitationEffect(0.5);
			JOptionPane.showMessageDialog(this, "Sanitation Protocols Activated! Infection probability during transport reduced by 50%.");
		}));

		upgradeList.add(new Upgrade("Rapid Testing", 1, "Reopen transport routes faster after infection levels drop.", () -> {
			Transport.setRapidTesting(true);
			JOptionPane.showMessageDialog(this, "Rapid Testing Deployed! Transport routes will reopen faster after infection drops.");
		}));

		upgradeList.add(new Upgrade("Infection-Free Zones", 1, "Keep routes between infection-free countries open.", () -> {
			markInfectionFreeZones();
			JOptionPane.showMessageDialog(this, "Infection-Free Zone protocols established. Routes between infection-free countries will remain open.");
		}));

		upgradeList.add(new Upgrade("Vaccine Distribution Networks", 1, "Prioritize vaccine delivery routes.", () -> {
			Transport.setVaccinePriority(true);
			JOptionPane.showMessageDialog(this, "Vaccine Distribution Networks established. Vaccine delivery routes will be prioritized.");
		}));

		upgradeList.add(new Upgrade("Media Campaign", 1, "Delay route closures by calming public fears.", () -> {
			GameWindow.adjustGlobalAwareness(-10);
			JOptionPane.showMessageDialog(this, "Media Campaign launched. Public awareness lowered, delaying potential route closures.");
		}));

		return upgradeList;
	}


	private void openUpgradeStore() {
		UpgradeStoreDialog store = new UpgradeStoreDialog(this, upgrades, points);
		store.setVisible(true);

		// Update remaining points after closing the store
		points = store.getRemainingPoints();
	}

	private List<Country> initializeCountries(JPanel mapPanel) {
		List<Country> countryList = new ArrayList<>();

		// Countries with their coordinates, continent assignments, infection rate, and population
		Object[][] countryData = {
				{"USA", 100, 100, "North America", 0.1, 331000000, 9833520.0},
				{"Canada", 200, 50, "North America", 0.08, 38000000, 9984670.0},
				{"Mexico", 150, 200, "North America", 0.09, 126000000, 1964375.0},
				{"Brazil", 250, 300, "South America", 0.12, 213000000, 8515767.0},
				{"UK", 500, 50, "Europe", 0.1, 68000000, 243610.0},
				{"France", 550, 100, "Europe", 0.1, 65000000, 551695.0},
				{"Germany", 600, 150, "Europe", 0.1, 83000000, 357022.0},
				{"India", 700, 300, "Asia", 0.15, 1390000000, 3287263.0},
				{"China", 750, 200, "Asia", 0.15, 1440000000, 9596961.0},
				{"Australia", 800, 400, "Australia", 0.1, 26000000, 7692024.0}
		};


		for (Object[] data : countryData) {
			String name = (String) data[0];
			int x = (int) data[1];
			int y = (int) data[2];
			String continent = (String) data[3];
			double infectionRate = (double) data[4] + this.infectionRate;
			int population = (int) data[5];
			double area = (double) data[6];

			Country country = new Country(name, x, y, continent, infectionRate, population, area);
			country.addToPanel(mapPanel);
			countryList.add(country);
		}

		return countryList;
	}

	private void markInfectionFreeZones() {
		SwingUtilities.invokeLater(() -> {
			for (Country country : countries) {
				if (!country.isInfected()) {
					System.out.println(country.getName() + " is infection-free.");
				}
			}
			JOptionPane.showMessageDialog(
					this,
					"Infection-Free Zones updated. Transport routes between infection-free countries will remain active.",
					"Infection-Free Zones",
					JOptionPane.INFORMATION_MESSAGE
			);
		});
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
		randomTransportTimer = new Timer(1000, e -> {
			if (!transports.isEmpty()) {
				Transport randomTransport = transports.get((int) (Math.random() * transports.size()));
				if (randomTransport.isRouteOperational()) {
					randomTransport.startTransport(false); // Regular transport
				}
			}
		});
		randomTransportTimer.start();
	}

	private void startVaccineTransport() {
		randomTransportTimer = new Timer(15000, e -> {
			if (!transports.isEmpty()) {
				Transport randomTransport = transports.get((int) (Math.random() * transports.size()));
				if (randomTransport.isRouteOperational()) {
					randomTransport.startTransport(true); // Vaccine transport
				}
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
					isInfectionStarted = true;
					startMainTimer();
					startIconSpawner();
					startInfectionRateIncrease();
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

	private void startInfectionRateIncrease() {
		infectionRateTimer = new Timer(30000, e -> {
			infectionRate += 0.01; // Increase infection rate by 0.01 (1%)
			JOptionPane.showMessageDialog(this, "The virus has mutated! Infection rate increased.",
					"Mutation Alert", JOptionPane.WARNING_MESSAGE);
		});
		infectionRateTimer.start();
	}

	private void startLaboratoryProgress() {
		laboratoryCount++;
		if (labTimer == null) {
			labTimer = new Timer(7000, e -> {
				int currentProgress = vaccineProgressBar.getValue();
				if (currentProgress < 100) {
					vaccineProgressBar.setValue(Math.min(currentProgress + laboratoryCount, 100)); // Add 1% progress every 2 seconds
				} else {
					labTimer.stop();
				}
			});
			labTimer.start();
		}
	}

	private void startIconSpawner() {
		iconSpawnerTimer = new Timer(12000, e -> {
			if (!isInfectionStarted || countries.isEmpty()) return;

			// Choose a random country
			Country randomCountry = countries.get((int) (Math.random() * countries.size()));
			spawnPointIcon(randomCountry);
		});
		iconSpawnerTimer.start(); // Start the timer when infection begins
	}

	private void spawnPointIcon(Country country) {
		JButton icon = new JButton(); // Create a button for the icon
		int offsetX = (int) (Math.random() * 50 - 25); // Random offset
		int offsetY = (int) (Math.random() * 50 - 25);
		int iconSize = 25;

		// Set position and size
		icon.setBounds(country.getX() + offsetX, country.getY() + offsetY, iconSize, iconSize);

		// Determine points and set text
		boolean isInfected = country.isInfected();
		int pointsEarned = isInfected ? 5 : 10;
		String iconText = "+" + pointsEarned;
		icon.setText(iconText);

		// Set color based on infection status
		icon.setBackground(isInfected ? Color.YELLOW : Color.BLUE);
		icon.setForeground(isInfected ? Color.BLACK : Color.WHITE); // Text color
		icon.setFont(new Font("Arial", Font.BOLD, 10)); // Readable font
		icon.setOpaque(true);
		icon.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// Add to layered pane
		layeredPane.add(icon, JLayeredPane.POPUP_LAYER);
		layeredPane.revalidate();
		layeredPane.repaint();
		activeIcons.add(icon);

		// Add click listener
		icon.addActionListener(e -> {
			score += pointsEarned; // Update total score
			points += pointsEarned; // Update spendable points
			scoreLabel.setText("Score: " + score); // Display total score

			// Remove the icon
			layeredPane.remove(icon);
			layeredPane.revalidate();
			layeredPane.repaint();
			activeIcons.remove(icon);
		});

		// Remove icon after 10 seconds if not clicked
		Timer removeTimer = new Timer(10000, ev -> {
			layeredPane.remove(icon);
			layeredPane.revalidate();
			layeredPane.repaint();
			activeIcons.remove(icon);
		});
		removeTimer.setRepeats(false);
		removeTimer.start();
	}


	public static int getGlobalAwareness() {
		return globalAwareness;
	}

	public static void adjustGlobalAwareness(int delta) {
		globalAwareness = Math.max(0, globalAwareness + delta);
	}

	private void updateGlobalAwareness() {
		int totalInfected = 0;
		int totalPopulation = 0;

		for (Country country : countries) {
			totalInfected += country.getInfectedPopulation();
			totalPopulation += country.getNormalPopulation();
		}

		int newAwareness = (int) ((double) totalInfected / totalPopulation * 100);
		adjustGlobalAwareness(newAwareness - globalAwareness); // Adjust awareness gradually
	}

	private void updateGameLogic() {
		for (Country country : countries) {
			if (country.isInfected()) {
				country.updateInfection();
			}
			if (country.isVaccinated()) {
				country.updateVaccination();
			}
		}

		// Update global awareness after infection updates
		updateGlobalAwareness();

		isGameOver();
	}

	public void isGameOver() {
		boolean allInfected = true;
		boolean noInfectionsLeft = true;

		for (Country country : countries) {
			if (country.getInfectedPopulation() > 0) {
				noInfectionsLeft = false;
			}
			if (!country.isAllInfected()) {
				allInfected = false;
			}
		}

		if (allInfected) {
			endGame(false); // Game lost
		}

		if (noInfectionsLeft) {
			endGame(true); // Game won
		}
	}

	private void endGame(boolean isVictory) {
		// Stop timers and animations
		if (timer != null) {
			timer.stop();
		}
		if (randomTransportTimer != null) {
			randomTransportTimer.stop();
		}
		for (Transport transport : transports) {
			transport.stopAnimationManually();
		}

		if (isVictory) {
			JOptionPane.showMessageDialog(this,
					"Congratulations! You have eradicated the virus.\nYour Score: " + score,
					"Victory", JOptionPane.INFORMATION_MESSAGE);
			PlayerNameDialog dialog = new PlayerNameDialog(this);
			dialog.setVisible(true);

			if (dialog.isConfirmed()) {
				String playerName = dialog.getPlayerName();
				HighScoreManager.getInstance().addHighScore(playerName, score, difficulty);

				JOptionPane.showMessageDialog(null,
						"High score saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null,
						"High score not saved.", "Notice", JOptionPane.WARNING_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this,
					"Game Over! The entire world has been infected.\nYour Score: " + score,
					"Defeat", JOptionPane.ERROR_MESSAGE);
		}

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

	private void startMainTimer() {
		if (timer != null && isInfectionStarted) {
			timer.start();
		}
	}

	private void setupMainTimer() {
		timer = new Timer(1000, e -> {
			timeElapsed++;
			timerLabel.setText("Time: " + timeElapsed + "s");
			updateGameLogic();
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new GameWindow("Medium")); // Test with medium difficulty
	}
}
