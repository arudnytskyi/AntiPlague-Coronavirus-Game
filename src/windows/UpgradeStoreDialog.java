package windows;

import utilities.Upgrade;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UpgradeStoreDialog extends JDialog {
	private int points;
	private JLabel pointsLabel;

	public UpgradeStoreDialog(JFrame parent, List<Upgrade> upgrades, int points) {
		super(parent, "Upgrade Store", true);
		this.points = points;

		setSize(400, 300);
		setLocationRelativeTo(parent);
		setLayout(new BorderLayout());

		// Points display
		pointsLabel = new JLabel("Points: " + points);
		pointsLabel.setFont(new Font("Arial", Font.BOLD, 16));
		pointsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(pointsLabel, BorderLayout.NORTH);

		// Upgrades list
		JPanel upgradePanel = new JPanel(new GridLayout(upgrades.size(), 1, 5, 5));
		for (Upgrade upgrade : upgrades) {
			JButton upgradeButton = new JButton(upgrade.getName() + " - " + upgrade.getCost() + " Points");
			upgradeButton.setToolTipText(upgrade.getDescription());
			upgradeButton.addActionListener(e -> purchaseUpgrade(upgrade));
			upgradePanel.add(upgradeButton);
		}
		add(new JScrollPane(upgradePanel), BorderLayout.CENTER);

		// Close button
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(e -> dispose());
		add(closeButton, BorderLayout.SOUTH);
	}

	private void purchaseUpgrade(Upgrade upgrade) {
		if (points >= upgrade.getCost()) {
			points -= upgrade.getCost();
			pointsLabel.setText("Points: " + points);
			upgrade.getEffect().apply();
			JOptionPane.showMessageDialog(this, "Purchased: " + upgrade.getName(), "Upgrade Successful", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Not enough points!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public int getRemainingPoints() {
		return points;
	}
}
