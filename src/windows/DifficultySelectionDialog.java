package windows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DifficultySelectionDialog extends JDialog {
	public DifficultySelectionDialog(JFrame parent) {
		super(parent, "Select Difficulty", true);
		setSize(300, 200);
		setLocationRelativeTo(parent);

		// Main panel
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4, 1, 10, 10));
		panel.setBackground(Color.LIGHT_GRAY);

		// Label
		JLabel label = new JLabel("Choose Difficulty Level:", SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.BOLD, 16));
		panel.add(label);

		// Difficulty buttons
		JButton easyButton = new JButton("Easy");
		JButton mediumButton = new JButton("Medium");
		JButton hardButton = new JButton("Hard");

		panel.add(easyButton);
		panel.add(mediumButton);
		panel.add(hardButton);

		// Add panel to dialog
		add(panel);

		// Button listeners
		easyButton.addActionListener(e -> startGame("Easy"));
		mediumButton.addActionListener(e -> startGame("Medium"));
		hardButton.addActionListener(e -> startGame("Hard"));
	}

	private void startGame(String difficulty) {
		dispose();
		new GameWindow(difficulty); // Pass difficulty to the game window
	}
}
