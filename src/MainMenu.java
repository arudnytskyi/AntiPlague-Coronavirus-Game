import utilities.HighScoreManager;
import windows.DifficultySelectionDialog;
import windows.HighScoresWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainMenu extends JFrame {
	private final HighScoreManager highScoreManager;

	public MainMenu() {
		setTitle("AntiPlague Coronavirus Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		highScoreManager = new HighScoreManager();

		// Center panel for buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		// New Game Button
		JButton newGameButton = createMenuButton("New Game");
		gbc.gridy = 0;
		buttonPanel.add(newGameButton, gbc);

		JButton highScoresButton = createMenuButton("High Scores");
		gbc.gridy = 1;
		buttonPanel.add(highScoresButton, gbc);

		JButton exitButton = createMenuButton("Exit");
		gbc.gridy = 2;
		buttonPanel.add(exitButton, gbc);

		newGameButton.addActionListener(e -> startNewGame());
		highScoresButton.addActionListener(e -> showHighScores());
		exitButton.addActionListener(e -> System.exit(0));

		add(buttonPanel, BorderLayout.CENTER);

		JLabel titleLabel = new JLabel("Welcome to AntiPlague", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		add(titleLabel, BorderLayout.NORTH);

		setPreferredSize(new Dimension(400, 300));
		pack();
		setLocationRelativeTo(null);

		addKeyBindings();
	}

	private JButton createMenuButton(String text) {
		JButton button = new JButton(text);
		button.setFont(new Font("Arial", Font.PLAIN, 18));
		button.setFocusPainted(false);
		button.setPreferredSize(new Dimension(200, 50));

		return button;
	}

	private void startNewGame() {
		DifficultySelectionDialog dialog = new DifficultySelectionDialog(this);
		dialog.setVisible(true);
	}

	private void showHighScores() {
		HighScoresWindow highScoresWindow = new HighScoresWindow(this, highScoreManager);
		highScoresWindow.setVisible(true);
	}

	private void addKeyBindings() {
		JRootPane rootPane = this.getRootPane();
		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = rootPane.getActionMap();

		KeyStroke keyStroke = KeyStroke.getKeyStroke("ctrl shift Q");

		inputMap.put(keyStroke, "returnToMenu");
		actionMap.put("returnToMenu", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Ctrl+Shift+Q pressed. Returning to main menu.");
				// Add logic to return to main menu
				// For now, just ensure this MainMenu window is visible
				setVisible(true);
			}
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainMenu mainMenu = new MainMenu();
			mainMenu.setVisible(true);
		});
	}
}
