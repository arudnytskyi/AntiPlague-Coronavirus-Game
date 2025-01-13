package utilities;

import javax.swing.*;
import java.awt.*;

public class Country {
	private String name;
	private int x, y;
	private String continent;
	private boolean infected = false; // Whether the country has any infection
	private JButton button;
	private double infectionRate; // Rate of infection spread within the country
	private int population;
	private int infectedPopulation;

	public Country(String name, int x, int y, String continent, double infectionRate, int population) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.continent = continent;
		this.infectionRate = infectionRate;
		this.population = population;
		this.infectedPopulation = 0;

		// Create a button to represent the country
		button = new JButton(getButtonText());
		button.setBounds(x, y, 120, 50);
		button.addActionListener(e -> interact());
		updateButtonAppearance();
	}

	public void addToPanel(JPanel panel) {
		panel.add(button);
	}

	private void interact() {
		String message = infected
				? name + " is infected! Infected Population: " + infectedPopulation + "/" + population
				: name + " is safe. Population: " + population;
		JOptionPane.showMessageDialog(null, message, "Country Status", JOptionPane.INFORMATION_MESSAGE);
	}

	public void updateInfection() {
		if (infected && infectedPopulation < population) {
			// Calculate new infections as a percentage of the remaining uninfected population
			int newInfections = (int) Math.ceil((infectionRate * (population - infectedPopulation)) / 10); // Spread more gradually
			infectedPopulation += newInfections;
			infectedPopulation = Math.min(infectedPopulation, population); // Ensure it doesn't exceed the total population

			// Update the appearance of the country
			updateButtonAppearance();
		}
	}

	private void updateButtonAppearance() {
		double infectionPercentage = (double) infectedPopulation / population * 100;
		if (infected) {
			button.setBackground(Color.RED);
			button.setForeground(Color.WHITE);
			button.setText(getButtonText());
		} else {
			button.setBackground(null);
			button.setForeground(Color.BLACK);
			button.setText(getButtonText());
		}
	}

	private String getButtonText() {
		if (infected) {
			double infectionPercentage = (double) infectedPopulation / population * 100;
			return name + " (" + (int) infectionPercentage + "% Infected)";
		} else {
			return name + " (Population: " + population + ")";
		}
	}

	public boolean isInfected() {
		return infected;
	}

	public void setInfected(boolean infected) {
		this.infected = infected;
		if (infected && infectedPopulation == 0) {
			infectedPopulation = 1; // Start with at least one infected individual
		}
		updateButtonAppearance();
	}

	public double getInfectionRate() {
		return infectionRate;
	}

	public int getInfectedPopulation() {
		return infectedPopulation;
	}

	public int getPopulation() {
		return population;
	}

	public String getName() {
		return name;
	}

	public String getContinent() {
		return continent;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
