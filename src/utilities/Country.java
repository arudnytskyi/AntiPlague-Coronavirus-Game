package utilities;

import javax.swing.*;
import java.awt.*;

public class Country {
	private String name;
	private int x, y;
	private String continent;
	private boolean infected = false;
	private JButton button;
	private double infectionRate;

	public Country(String name, int x, int y, String continent, double infectionRate) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.continent = continent;
		this.infectionRate = infectionRate;

		// Create a button to represent the country
		button = new JButton(name);
		button.setBounds(x, y, 100, 50);
		button.addActionListener(e -> interact());
		updateButtonAppearance();
	}

	public void addToPanel(JPanel panel) {
		panel.add(button);
	}

	private void interact() {
		String message = infected ? name + " is infected! Take action!" : name + " is safe.";
		JOptionPane.showMessageDialog(null, message, "Country Status", JOptionPane.INFORMATION_MESSAGE);
	}

	public void updateInfection() {
		if (!infected && Math.random() < infectionRate) { // Use the infection rate
			infected = true;
			updateButtonAppearance();
		}
	}

	private void updateButtonAppearance() {
		if (infected) {
			button.setBackground(Color.RED);
			button.setForeground(Color.WHITE);
		} else {
			button.setBackground(null);
			button.setForeground(Color.BLACK);
		}
	}

	public boolean isInfected() {
		return infected;
	}

	public void setInfected(boolean infected) {
		this.infected = infected;
		updateButtonAppearance();
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getContinent() {
		return continent;
	}
}
