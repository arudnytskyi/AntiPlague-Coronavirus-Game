package windows;

import utilities.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameWindow extends JFrame {
	private final List<Country> countries;
	private List<Transport> transports;
	private Thread transportThread;
	private final List<Upgrade> upgrades;
	private final List<JButton> activeIcons;
	private final JPanel mapPanel;
	private final JLayeredPane layeredPane;
	private final JLabel scoreLabel;
	private final JProgressBar vaccineProgressBar;
	private final JLabel timerLabel;
	private final GameTimerManager timerManager;
	private ScheduledFuture<?> randomTransportTask;
	private ScheduledFuture<?> infectionRateTask;
	private ScheduledFuture<?> labProgressTask;
	private ScheduledFuture<?> iconSpawnerTask;
	private ScheduledFuture<?> vaccineTransportTask;
	private ScheduledFuture<?> gameTimerTask;
	private int score = 0;
	private int points = 0;
	private final String difficulty;
	private double infectionRate;
	private static int globalAwareness = 0;
	private int laboratoryCount = 0;
	private boolean isInfectionStarted = false;
	private boolean vaccineDistribution = false;

	public GameWindow(String difficulty) {
		this.difficulty = difficulty;

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

		setTitle("AntiPlague Game - " + difficulty + " Mode");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 600);
		setLocationRelativeTo(null);
		setResizable(false);

		timerManager = new GameTimerManager();

		JPanel panel = new JPanel(new BorderLayout());

		JPanel topPanel = new JPanel(new GridLayout(1, 4));

		scoreLabel = new JLabel("Score: 0");
		scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
		topPanel.add(scoreLabel);

		timerLabel = new JLabel("Time: 0s");
		timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
		topPanel.add(timerLabel);

		vaccineProgressBar = new JProgressBar(0, 100);
		vaccineProgressBar.setValue(0);
		vaccineProgressBar.setStringPainted(true);
		vaccineProgressBar.setFont(new Font("Arial", Font.BOLD, 12));
		topPanel.add(vaccineProgressBar);

		JButton upgradeButton = new JButton("Upgrades");
		upgradeButton.addActionListener(e -> openUpgradeStore());
		topPanel.add(upgradeButton);

		panel.add(topPanel, BorderLayout.NORTH);

		mapPanel = new JPanel(null);
		layeredPane = new JLayeredPane();
		layeredPane.setLayout(null);
		layeredPane.setBounds(0, 0, 800, 500);
		mapPanel.add(layeredPane);
		panel.add(mapPanel, BorderLayout.CENTER);

		upgrades = initializeUpgrades();
		countries = initializeCountries(mapPanel);
		activeIcons = new ArrayList<>();

		promptForFirstInfectedCountry();

		JPanel controlPanel = new JPanel();
		JButton quitButton = new JButton("Quit");
		quitButton.addActionListener(e -> quitGame());
		controlPanel.add(quitButton);
		panel.add(controlPanel, BorderLayout.SOUTH);

		add(panel);

		addKeyBindings(panel);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int newWidth = mapPanel.getWidth();
				int newHeight = mapPanel.getHeight();

				layeredPane.setBounds(0, 0, newWidth, newHeight);

				mapPanel.revalidate();

				if (transports != null) {
					adjustComponentSizes();
				}
			}
		});

		setResizable(true);
		setVisible(true);
	}

	private void adjustComponentSizes() {
		SwingUtilities.invokeLater(() -> {
			int newWidth = layeredPane.getWidth();
			int newHeight = layeredPane.getHeight();

			double widthScale = (double) newWidth / 800;
			double heightScale = (double) newHeight / 500;

			for (Country country : countries) {
				int scaledX = (int) (country.getOriginalX() * widthScale);
				int scaledY = (int) (country.getOriginalY() * heightScale);
				country.setPosition(scaledX, scaledY);
			}

			for (JButton icon : activeIcons) {
				Country country = (Country) icon.getClientProperty("country");
				int offsetX = (int) icon.getClientProperty("offsetX");
				int offsetY = (int) icon.getClientProperty("offsetY");

				int scaledX = (int) (country.getOriginalX() * widthScale) + offsetX;
				int scaledY = (int) (country.getOriginalY() * heightScale) + offsetY;

				icon.setBounds(scaledX, scaledY, icon.getWidth(), icon.getHeight());
			}

			for (Transport transport : transports) {
				Point originPoint = transport.getOrigin().getPosition();
				Point destinationPoint = transport.getDestination().getPosition();
				transport.updateRoute(originPoint, destinationPoint);
			}

			layeredPane.repaint();
		});
	}

	private List<Upgrade> initializeUpgrades() {
		List<Upgrade> upgradeList = new ArrayList<>();

		upgradeList.add(new Upgrade("Vaccine Research", 1, "Adds +5% to vaccine development.", () -> {
			int currentProgress = vaccineProgressBar.getValue();
			vaccineProgressBar.setValue(Math.min(currentProgress + 5, 100));
			JOptionPane.showMessageDialog(this, "Vaccine research progressed by +5%.");
		}));

		upgradeList.add(new Upgrade("Build Laboratory", 1, "Adds a laboratory that increases vaccine progress over time.", () -> {
			laboratoryCount++;
			JOptionPane.showMessageDialog(this, "Laboratory built! Vaccine progress will now increase over time.");
		}));

		upgradeList.add(new Upgrade("Vaccine Distribution", 1, "Enable vaccine distribution via transport.", () -> {
			vaccineDistribution = true;
			JOptionPane.showMessageDialog(this, "Vaccine distribution via transport enabled.");
		}));

		upgradeList.add(new Upgrade("Cancel Mutation", 1, "Decreases the infection rate by 0.01 (1%).", () -> {
			if (infectionRate > 0.5) {
				infectionRate -= 0.3;
				for (Country country : countries) country.setInfectionRate(infectionRate);
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
			Transport.setInfectionFreeZones(true);
			JOptionPane.showMessageDialog(this, "Infection-Free Zone protocols established. Routes between infection-free countries will remain open.");
		}));

		upgradeList.add(new Upgrade("Vaccine Distribution Networks", 1, "Prioritize vaccine delivery routes.", () -> {
			Transport.setVaccinePriority(true);
			JOptionPane.showMessageDialog(this, "Vaccine Distribution Networks established. Vaccine delivery routes will be prioritized.");
		}));

		upgradeList.add(new Upgrade("Media Campaign", 1, "Delay route closures by calming public fears.", () -> {
			GameWindow.adjustGlobalAwareness(-30);
			JOptionPane.showMessageDialog(this, "Media Campaign launched. Public awareness lowered, delaying potential route closures.");
		}));

		return upgradeList;
	}


	private void openUpgradeStore() {
		UpgradeStoreDialog store = new UpgradeStoreDialog(this, upgrades, points);
		store.setVisible(true);

		points = store.getRemainingPoints();
	}

	private List<Country> initializeCountries(JPanel mapPanel) {
		List<Country> countryList = new ArrayList<>();

		// Countries with their coordinates, continent assignments, infection rate, and population
		Object[][] countryData = {
				{"USA", 50, 100, "North America", 0.12, 331000000, 9833520.0},
				{"Canada", 150, 50, "North America", 0.08, 38000000, 9984670.0},
				{"Mexico", 90, 200, "North America", 0.09, 126000000, 1964375.0},
				{"Brazil", 190, 300, "South America", 0.14, 213000000, 8515767.0},
				{"UK", 450, 50, "Europe", 0.11, 68000000, 243610.0},
				{"France", 490, 100, "Europe", 0.1, 65000000, 551695.0},
				{"Germany", 550, 150, "Europe", 0.07, 83000000, 357022.0},
				{"India", 650, 300, "Asia", 0.15, 1390000000, 3287263.0},
				{"China", 700, 200, "Asia", 0.13, 1440000000, 9596961.0},
				{"Australia", 730, 400, "Australia", 0.06, 26000000, 7692024.0}
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

	private List<Transport> initializeTransports(JPanel mapPanel) {
		List<Transport> transportList = new ArrayList<>();

		for (int i = 0; i < countries.size(); i++) {
			for (int j = 0; j < countries.size(); j++) {
				if (i == j) continue;

				Country origin = countries.get(i);
				Country destination = countries.get(j);

				// Plane: (A -> B or B -> A)
				if (origin.getName().compareTo(destination.getName()) < 0) {
					transportList.add(new Transport("Airline", origin, destination, mapPanel));

					// Ship: (A -> B or B -> A)
					if (!origin.getContinent().equals(destination.getContinent())) {
						transportList.add(new Transport("Ship", origin, destination, mapPanel));
					}
				}

				// Train: (A -> B and B -> A)
				if (origin.getContinent().equals(destination.getContinent())) {
					transportList.add(new Transport("Train", origin, destination, mapPanel));
				}
			}
		}

		return transportList;
	}


	private void promptForFirstInfectedCountry() {
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(
					this,
					"Please select the first infected country by clicking on the map.",
					"Initial Infection Selection",
					JOptionPane.INFORMATION_MESSAGE
			);

			for (Country country : countries) {
				country.setSelectable(true);
			}
			transportThread = new Thread(() -> transports = initializeTransports(mapPanel));
			transportThread.start();

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
						Thread.sleep(100);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return;
					}
				}

				SwingUtilities.invokeLater(() -> {
					for (Country country : countries) {
						country.setSelectable(false);
					}
					isInfectionStarted = true;
					startTimers();
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

	private void startTimers() {
		gameTimerTask = timerManager.scheduleAtFixedRate(() -> SwingUtilities.invokeLater(() -> {
			updateGameLogic();
			int elapsedTime = Integer.parseInt(timerLabel.getText().replace("Time: ", "").replace("s", "")) + 1;
			timerLabel.setText("Time: " + elapsedTime + "s");
		}), 0, 1, TimeUnit.SECONDS);

		infectionRateTask = timerManager.scheduleAtFixedRate(() -> {
			infectionRate += 0.01;
			for (Country country : countries) country.setInfectionRate(infectionRate);
			SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
					null,
					"The virus has mutated! Infection rate increased.",
					"Mutation Alert",
					JOptionPane.WARNING_MESSAGE
			));
		}, 30, 30, TimeUnit.SECONDS);

		labProgressTask = timerManager.scheduleAtFixedRate(() -> {
			if (laboratoryCount > 0) {
				SwingUtilities.invokeLater(() -> {
					int currentProgress = vaccineProgressBar.getValue();
					vaccineProgressBar.setValue(Math.min(currentProgress + laboratoryCount, 100));
				});
			}
		}, 7, 7, TimeUnit.SECONDS);

		iconSpawnerTask = timerManager.scheduleAtFixedRate(() -> {
			if (isInfectionStarted && !countries.isEmpty()) {
				Country randomCountry = countries.get((int) (Math.random() * countries.size()));
				SwingUtilities.invokeLater(() -> spawnPointIcon(randomCountry));
			}
		}, 8, 8, TimeUnit.SECONDS);

		randomTransportTask = timerManager.scheduleAtFixedRate(() -> {
			if (!transports.isEmpty()) {
				Transport randomTransport = transports.get((int) (Math.random() * transports.size()));
				if (randomTransport.isRouteOperational()) {
					randomTransport.startTransport(false);
				}
			}
		}, 3, 3, TimeUnit.SECONDS);

		vaccineTransportTask = timerManager.scheduleAtFixedRate(() -> {
			if (!transports.isEmpty() && vaccineDistribution) {
				Transport randomTransport = transports.get((int) (Math.random() * transports.size()));
				if (randomTransport.isRouteOperational()) {
					randomTransport.startTransport(true);
				}
			}
		}, 7, 7, TimeUnit.SECONDS);
	}

	private void spawnPointIcon(Country country) {
		JButton icon = new JButton();
		int offsetX = (int) (Math.random() * 50 - 25);
		int offsetY = (int) (Math.random() * 50 - 25);
		int iconSize = 25;

		// Set the initial position of the icon relative to the country's position
		int iconX = country.getX() + offsetX;
		int iconY = country.getY() + offsetY;
		icon.setBounds(iconX, iconY, iconSize, iconSize);

		// Icon styling
		boolean isInfected = country.isInfected();
		int pointsEarned = isInfected ? 5 : 10;
		icon.setText("+" + pointsEarned);
		icon.setBackground(isInfected ? Color.YELLOW : Color.BLUE);
		icon.setForeground(isInfected ? Color.BLACK : Color.WHITE);
		icon.setFont(new Font("Arial", Font.BOLD, 10));
		icon.setOpaque(true);
		icon.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// Add icon to the layeredPane and activeIcons list
		layeredPane.add(icon, JLayeredPane.POPUP_LAYER);
		layeredPane.revalidate();
		layeredPane.repaint();
		activeIcons.add(icon);

		// Attach action listener to the icon
		icon.addActionListener(e -> {
			score += pointsEarned;
			points += pointsEarned;
			scoreLabel.setText("Score: " + score);
			layeredPane.remove(icon);
			layeredPane.revalidate();
			layeredPane.repaint();
			activeIcons.remove(icon);
		});

		// Timer to automatically remove the icon after 10 seconds
		Timer removeTimer = new Timer(10000, ev -> {
			layeredPane.remove(icon);
			layeredPane.revalidate();
			layeredPane.repaint();
			activeIcons.remove(icon);
		});
		removeTimer.setRepeats(false);
		removeTimer.start();

		// Store the association between the icon and the country
		icon.putClientProperty("country", country);
		icon.putClientProperty("offsetX", offsetX);
		icon.putClientProperty("offsetY", offsetY);
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

		if (allInfected && isInfectionStarted) {
			endGame(false);
		} else if (noInfectionsLeft && isInfectionStarted) {
			endGame(true);
		}
	}


	private void endGame(boolean isVictory) {
		stopAllTimers();
		for (Transport transport : transports) {
			transport.stopAnimationManually();
		}

		if (isVictory) {
			JOptionPane.showMessageDialog(this,
					"Congratulations! You have eradicated the virus.\nYour Score: " + score,
					"Victory", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
					"Game Over! The entire world has been infected.\nYour Score: " + score,
					"Defeat", JOptionPane.ERROR_MESSAGE);
		}

		addScore();
		dispose();
	}

	private void quitGame() {
		stopAllTimers();
		dispose();
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

	private void stopAllTimers() {
		if (infectionRateTask != null) infectionRateTask.cancel(true);
		if (labProgressTask != null) labProgressTask.cancel(true);
		if (iconSpawnerTask != null) iconSpawnerTask.cancel(true);
		if (randomTransportTask != null) randomTransportTask.cancel(true);
		if (vaccineTransportTask != null) vaccineTransportTask.cancel(true);
		if (gameTimerTask != null) gameTimerTask.cancel(true);
		transportThread.interrupt();
		timerManager.shutdown();
	}

	private void addScore() {
		PlayerNameDialog dialog = new PlayerNameDialog(this);
		dialog.setVisible(true);

		if (dialog.isConfirmed()) {
			String playerName = dialog.getPlayerName();
			HighScoreManager.getInstance().addHighScore(playerName, score, difficulty);

			JOptionPane.showMessageDialog(null,
					"Score saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null,
					"Score not saved.", "Notice", JOptionPane.WARNING_MESSAGE);
		}
	}
}
