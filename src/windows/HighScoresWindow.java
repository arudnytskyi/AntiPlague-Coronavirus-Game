package windows;

import utilities.HighScoreManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class HighScoresWindow extends JDialog {
	private JList<String> highScoreList;
	private HighScoreManager highScoreManager;
	public HighScoresWindow(JFrame parent, HighScoreManager highScoreManager) {
		super(parent, "High Scores", true);
		this.highScoreManager = highScoreManager;

		setLayout(new BorderLayout());

		JLabel title = new JLabel("High Scores", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 24));
		title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		add(title, BorderLayout.NORTH);

		DefaultListModel<String> listModel = new DefaultListModel<>();
		highScoreList = getHighScoreList(listModel);

		refreshHighScores();

		JScrollPane scrollPane = new JScrollPane(highScoreList);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(scrollPane, BorderLayout.CENTER);

		JPanel closeButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton closeButton = new JButton("Close");
		closeButton.setFont(new Font("Arial", Font.PLAIN, 18));
		closeButton.addActionListener(e -> dispose());
		closeButton.setPreferredSize(new Dimension(150, 40));
		closeButton.setFocusPainted(false);

		closeButtonPanel.add(closeButton);
		add(closeButtonPanel, BorderLayout.SOUTH);

		setPreferredSize(new Dimension(400, 350));
		pack();
		setLocationRelativeTo(null);
	}

	private static JList<String> getHighScoreList(DefaultListModel<String> listModel) {
		JList<String> highScoreList = new JList<>(listModel);
		highScoreList.setSelectionModel(new DefaultListSelectionModel() {
			@Override
			public void setSelectionInterval(int index0, int index1) {}
		});

		highScoreList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (index % 2 == 1) {
					c.setBackground(new Color(240, 240, 240));
				} else {
					c.setBackground(Color.WHITE);
				}
				c.setFont(new Font("Arial", Font.PLAIN, 18));
				return c;
			}
		});
		System.out.println(highScoreList);
		return highScoreList;
	}

	public void refreshHighScores() {
		DefaultListModel<String> model = new DefaultListModel<>();
		List<HighScoreManager.HighScore> highScores = highScoreManager.getHighScores(); // Fetch updated data
		for (HighScoreManager.HighScore score : highScores) {
			model.addElement(score.toString());
		}
		highScoreList.setModel(model); // Update the displayed list
	}

}