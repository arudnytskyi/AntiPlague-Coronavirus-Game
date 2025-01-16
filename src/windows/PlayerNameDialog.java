package windows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PlayerNameDialog extends JDialog {
	private final JTextField nameField;
	private boolean confirmed;
	private String playerName;

	public PlayerNameDialog(Frame owner) {
		super(owner, "Enter Your Name", true);

		setTitle("Enter Your Name");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		// Title styling similar to MainMenu
		JLabel titleLabel = new JLabel("Enter Your Name", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		add(titleLabel, BorderLayout.NORTH);

		// Main panel styling
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		nameField = new JTextField();
		nameField.setPreferredSize(new Dimension(200, 30));
		nameField.setFont(new Font("Arial", Font.PLAIN, 14));
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		mainPanel.add(nameField, gbc);

		add(mainPanel, BorderLayout.CENTER);

		// Button panel styling
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		JButton confirmButton = createDialogButton("OK");
		JButton cancelButton = createDialogButton("Cancel");

		confirmButton.addActionListener(getConfirmAction());
		cancelButton.addActionListener(e -> dispose());

		gbc.gridwidth = 1;
		gbc.gridy = 0;
		gbc.gridx = 0;
		buttonPanel.add(confirmButton, gbc);

		gbc.gridx = 1;
		buttonPanel.add(cancelButton, gbc);

		add(buttonPanel, BorderLayout.SOUTH);

		setPreferredSize(new Dimension(400, 200));
		pack();
		setLocationRelativeTo(owner);
	}

	private JButton createDialogButton(String text) {
		JButton button = new JButton(text);
		button.setFont(new Font("Arial", Font.PLAIN, 18));
		button.setFocusPainted(false);
		button.setPreferredSize(new Dimension(120, 40));
		return button;
	}

	private ActionListener getConfirmAction() {
		return e -> {
			playerName = nameField.getText().trim();
			if (!playerName.isEmpty()) {
				confirmed = true;
				dispose();
			} else {
				JOptionPane.showMessageDialog(this, "Name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		};
	}

	public String getPlayerName() {
		return playerName;
	}

	public boolean isConfirmed() {
		return confirmed;
	}
}