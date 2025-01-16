package windows;

import javax.swing.*;
import java.awt.*;

public class DifficultySelectionDialog extends JDialog {
	public DifficultySelectionDialog(JFrame parent) {
		super(parent, "Select Difficulty", true);
		setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		JButton easyButton = createMenuButton("Easy");
		gbc.gridy = 0;
		buttonPanel.add(easyButton, gbc);

		JButton mediumButton = createMenuButton("Medium");
		gbc.gridy = 1;
		buttonPanel.add(mediumButton, gbc);

		JButton hardButton = createMenuButton("Hard");
		gbc.gridy = 2;
		buttonPanel.add(hardButton, gbc);

		JButton backButton = createMenuButton("Back");
		gbc.gridy = 3;
		buttonPanel.add(backButton, gbc);

		add(buttonPanel, BorderLayout.CENTER);

		JLabel titleLabel = new JLabel("Choose Difficulty Level:", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		add(titleLabel, BorderLayout.NORTH);

		setPreferredSize(new Dimension(400, 350));
		pack();
		setLocationRelativeTo(null);

		easyButton.addActionListener(e -> startGame("Easy"));
		mediumButton.addActionListener(e -> startGame("Medium"));
		hardButton.addActionListener(e -> startGame("Hard"));

		backButton.addActionListener(e -> goBackToMainMenu());
	}

	private JButton createMenuButton(String text) {
		JButton button = new JButton(text);
		button.setFont(new Font("Arial", Font.PLAIN, 18));
		button.setFocusPainted(false);
		button.setPreferredSize(new Dimension(200, 50));

		return button;
	}

	private void startGame(String difficulty) {
		dispose();
		new GameWindow(difficulty);
	}

	private void goBackToMainMenu() {
		dispose();
	}
}