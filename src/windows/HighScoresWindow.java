package windows;

import utilities.HighScoreManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HighScoresWindow extends JDialog {
	public HighScoresWindow(JFrame parent, HighScoreManager highScoreManager) {
		super(parent, "High Scores", true);
		setSize(400, 300);
		setLocationRelativeTo(parent);

		// Main panel
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.LIGHT_GRAY);

		// Title
		JLabel title = new JLabel("High Scores", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 20));
		title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		panel.add(title, BorderLayout.NORTH);

		// High Scores List
		DefaultListModel<String> listModel = new DefaultListModel<>();
		List<HighScoreManager.HighScore> highScores = highScoreManager.getHighScores();
		for (HighScoreManager.HighScore score : highScores) {
			listModel.addElement(score.toString());
		}

		JList<String> highScoreList = new JList<>(listModel);
		highScoreList.setFont(new Font("Arial", Font.PLAIN, 16));
		JScrollPane scrollPane = new JScrollPane(highScoreList);
		panel.add(scrollPane, BorderLayout.CENTER);

		// Close Button
		JButton closeButton = new JButton("Close");
		closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
		closeButton.addActionListener(e -> dispose());
		panel.add(closeButton, BorderLayout.SOUTH);

		// Add panel to dialog
		add(panel);
	}
}
