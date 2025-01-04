package windows;

import utilities.Country;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class GameWindow extends JFrame {
	private List<Country> countries;
	private JLabel scoreLabel;
	private JLabel timerLabel;
	private int score = 0;
	private int timeElapsed = 0;
	private Timer timer;
	private String difficulty;
	private double infectionRate;

	public GameWindow(String difficulty) {
		this.difficulty = difficulty;

		// Set infection rate based on difficulty
		switch (difficulty) {
			case "Easy":
				infectionRate = 0.05; // 5% chance of infection
				break;
			case "Medium":
				infectionRate = 0.1; // 10% chance of infection
				break;
			case "Hard":
				infectionRate = 0.2; // 20% chance of infection
				break;
		}

		// Set up the frame
		setTitle("AntiPlague Game - " + difficulty + " Mode");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
		setResizable(false);

		// Main panel for layout
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		// Top panel for score and timer
		JPanel topPanel = new JPanel(new GridLayout(1, 2));
		scoreLabel = new JLabel("Score: 0");
		timerLabel = new JLabel("Time: 0s");
		scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
		timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
		topPanel.add(scoreLabel);
		topPanel.add(timerLabel);
		panel.add(topPanel, BorderLayout.NORTH);

		// Map panel
		JPanel mapPanel = new JPanel();
		mapPanel.setLayout(null);
		panel.add(mapPanel, BorderLayout.CENTER);

		// Add countries to the map
		countries = initializeCountries(mapPanel);

		// Add control panel
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

	private List<Country> initializeCountries(JPanel mapPanel) {
		List<Country> countryList = new ArrayList<>();
		// Add 10 countries with positions
		String[] countryNames = {"USA", "Canada", "Mexico", "Brazil", "UK", "France", "Germany", "India", "China", "Australia"};
		int[][] positions = {
				{100, 100}, {150, 50}, {100, 200}, {200, 300}, {400, 50},
				{450, 100}, {500, 150}, {600, 300}, {700, 200}, {750, 400}
		};

		for (int i = 0; i < countryNames.length; i++) {
			Country country = new Country(countryNames[i], positions[i][0], positions[i][1], infectionRate);
			country.addToPanel(mapPanel);
			countryList.add(country);
		}

		return countryList;
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
		score++;
		scoreLabel.setText("Score: " + score);
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
}
