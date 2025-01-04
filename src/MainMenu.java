import utilities.HighScoreManager;
import windows.DifficultySelectionDialog;
import windows.HighScoresWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class MainMenu extends JFrame {
	private HighScoreManager highScoreManager; // Declare at class level

	public MainMenu() {
		// Initialize high score manager
		highScoreManager = new HighScoreManager();

		// Set up the frame
		setTitle("AntiPlague Coronavirus Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 300);
		setLocationRelativeTo(null); // Center the window
		setResizable(false);

		// Main panel for layout
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.DARK_GRAY);

		// Title label
		JLabel title = new JLabel("AntiPlague Game", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 24));
		title.setForeground(Color.WHITE);
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
		panel.add(title, BorderLayout.NORTH);

		// Button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
		buttonPanel.setBackground(Color.DARK_GRAY);

		// Buttons
		JButton newGameButton = new JButton("New Game");
		JButton highScoresButton = new JButton("High Scores");
		JButton exitButton = new JButton("Exit");

		// Style buttons
		Font buttonFont = new Font("Arial", Font.PLAIN, 18);
		newGameButton.setFont(buttonFont);
		highScoresButton.setFont(buttonFont);
		exitButton.setFont(buttonFont);

		// Add buttons to panel
		buttonPanel.add(newGameButton);
		buttonPanel.add(highScoresButton);
		buttonPanel.add(exitButton);

		// Add button panel to main panel
		panel.add(buttonPanel, BorderLayout.CENTER);

		// Add action listeners
		newGameButton.addActionListener(e -> showDifficultySelection());
		highScoresButton.addActionListener(e -> showHighScores());
		exitButton.addActionListener(e -> System.exit(0));

		// Add panel to frame
		add(panel);

		// Add Ctrl+Shift+Q shortcut
		addKeyBindings(panel);

		// Make frame visible
		setVisible(true);
	}

	// Display difficulty selection dialog
	private void showDifficultySelection() {
		DifficultySelectionDialog dialog = new DifficultySelectionDialog(this);
		dialog.setVisible(true);
	}

	// Show high scores
	private void showHighScores() {
		HighScoresWindow highScoresWindow = new HighScoresWindow(this, highScoreManager);
		highScoresWindow.setVisible(true);
	}

	// Add method to simulate adding a high score
	private void simulateAddHighScore(String playerName, int score) {
		highScoreManager.addHighScore(playerName, score);
	}

	// Add Ctrl+Shift+Q shortcut
	private void addKeyBindings(JPanel panel) {
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "quitToMenu");
		panel.getActionMap().put("quitToMenu", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(MainMenu.this, "Returning to Main Menu", "Shortcut Triggered", JOptionPane.INFORMATION_MESSAGE);
				// Logic for quitting the current game (if implemented)
			}
		});
	}

	// Main method to run the program
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainMenu mainMenu = new MainMenu();
			// Simulate adding some scores for testing
			mainMenu.simulateAddHighScore("Alice", 1200);
			mainMenu.simulateAddHighScore("Bob", 800);
			mainMenu.simulateAddHighScore("Charlie", 1500);
		});
	}
}
