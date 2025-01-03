import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
	public MainMenu() {
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
		newGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDifficultySelection();
			}
		});

		highScoresButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showHighScores();
			}
		});

		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		// Add panel to frame
		add(panel);

		// Make frame visible
		setVisible(true);
	}

	// Placeholder for difficulty selection
	private void showDifficultySelection() {
		JOptionPane.showMessageDialog(this, "Difficulty selection will be implemented later.", "New Game", JOptionPane.INFORMATION_MESSAGE);
	}

	// Placeholder for high scores
	private void showHighScores() {
		JOptionPane.showMessageDialog(this, "High Scores will be implemented later.", "High Scores", JOptionPane.INFORMATION_MESSAGE);
	}

	// Main method to run the program
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MainMenu());
	}
}
