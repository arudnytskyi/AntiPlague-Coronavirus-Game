package utilities;

import javax.swing.*;
import java.awt.*;

public class Country {
	private String name;
	private int x, y;
	private String continent;
	private boolean infected = false; // Whether the country has any infection
	private boolean selectable = false;
	private JButton button;
	private double infectionRate; // Rate of infection spread within the country
	private int population;
	private int infectedPopulation;
	private double area; // Area in square kilometers
	private boolean vaccinated = false; // Whether the country is vaccinated
	private int vaccinatedPopulation = 0;

	public Country(String name, int x, int y, String continent, double infectionRate, int population, double area) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.continent = continent;
		this.infectionRate = infectionRate;
		this.population = population;
		this.infectedPopulation = 0;
		this.area = area;

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
		if (selectable) {
			int confirm = JOptionPane.showConfirmDialog(
					null,
					"Set " + name + " as the first infected country?",
					"Confirm Selection",
					JOptionPane.YES_NO_OPTION
			);
			if (confirm == JOptionPane.YES_OPTION) {
				setInfected(true);
				selectable = false; // Lock selection
				JOptionPane.showMessageDialog(
						null,
						name + " is now infected. Initial infected: 1/" + population,
						"Infection Started",
						JOptionPane.INFORMATION_MESSAGE
				);
			}
		} else {
			String message = infected
					? name + " is infected! Infected Population: " + infectedPopulation + "/" + population
					: name + " is safe. Population: " + population;
			JOptionPane.showMessageDialog(null, message, "Country Status", JOptionPane.INFORMATION_MESSAGE);
		}
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

	public void updateVaccination() {
		if (vaccinated && vaccinatedPopulation < population) {
			// Increase vaccinated population and decrease infection proportionally
			int newVaccinated = (int) Math.ceil((infectionRate * (population - vaccinatedPopulation)) / 10);
			vaccinatedPopulation += newVaccinated;
			vaccinatedPopulation = Math.min(vaccinatedPopulation, population);
			infectedPopulation -= newVaccinated;
			infectedPopulation = Math.max(infectedPopulation, 0);

			updateButtonAppearance();
		}
	}

	public void setVaccinated(boolean vaccinated) {
		this.vaccinated = vaccinated;
		if (vaccinated && vaccinatedPopulation == 0) {
			vaccinatedPopulation = 1; // Start with at least one vaccinated individual
		}
		updateButtonAppearance();
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

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
		updateButtonAppearance(); // Update button visuals if needed
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

	public double getPopulationDensity() {
		return population / area;
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

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public boolean isVaccinated() {
		return vaccinated;
	}

	public int getVaccinatedPopulation() {
		return vaccinatedPopulation;
	}
}
