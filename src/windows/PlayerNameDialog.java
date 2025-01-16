package windows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerNameDialog extends JDialog {
	private final JTextField nameField;
	private boolean confirmed;
	private String playerName;

	public PlayerNameDialog(Frame owner) {
		super(owner, "Enter Your Name", true);
		setLayout(new BorderLayout());
		setSize(300, 150);
		setLocationRelativeTo(owner);

		JLabel label = new JLabel("Enter your name for the high score:");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		add(label, BorderLayout.NORTH);

		nameField = new JTextField();
		add(nameField, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		JButton confirmButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");

		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playerName = nameField.getText().trim();
				if (!playerName.isEmpty()) {
					confirmed = true;
					dispose();
				} else {
					JOptionPane.showMessageDialog(PlayerNameDialog.this,
							"Name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		cancelButton.addActionListener(e -> {
			confirmed = false;
			dispose();
		});

		buttonPanel.add(confirmButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	public String getPlayerName() {
		return playerName;
	}

	public boolean isConfirmed() {
		return confirmed;
	}
}
