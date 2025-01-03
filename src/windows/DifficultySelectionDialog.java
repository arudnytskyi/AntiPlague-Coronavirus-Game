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
		easyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startGame("Easy");
			}
		});

		mediumButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startGame("Medium");
			}
		});

		hardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startGame("Hard");
			}
		});
	}

	private void startGame(String difficulty) {
		JOptionPane.showMessageDialog(this, "Starting game on " + difficulty + " difficulty!", "Game Start", JOptionPane.INFORMATION_MESSAGE);
		dispose();
		// Logic for launching the game can be added here
	}
}
