package utilities;

import javax.swing.*;
import java.awt.*;

public class Country {
	private String name;
	private String continent;
	private JButton button;
	private int x, y;
	private boolean selectable = false;
	private boolean infected = false;
	private boolean vaccinated = false;
	private double area;
	private double infectionRate;
	private int normalPopulation;
	private int infectedPopulation;
	private int vaccinatedPopulation;

	public Country(String name, int x, int y, String continent, double infectionRate, int totalPopulation, double area) {
		this.name = name;
		this.continent = continent;
		this.area = area;
		this.x = x;
		this.y = y;
		this.infectionRate = infectionRate;
		this.normalPopulation = totalPopulation;
		this.infectedPopulation = 0;
		this.vaccinatedPopulation = 0;

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
				selectable = false;
			}
		} else {
			int totalPopulation = getTotalPopulation(); // Assuming this method gives the total population
			double normalPercentage = ((double) normalPopulation / totalPopulation) * 100;
			double infectedPercentage = ((double) infectedPopulation / totalPopulation) * 100;
			double vaccinatedPercentage = ((double) vaccinatedPopulation / totalPopulation) * 100;

			// Construct the message
			String message = String.format(
					"%s is infected!%nInfected Population: %d (%.2f%%)%n" +
							"Normal Population: %d (%.2f%%)%nVaccinated Population: %d (%.2f%%)",
					name, infectedPopulation, infectedPercentage,
					normalPopulation, normalPercentage,
					vaccinatedPopulation, vaccinatedPercentage
			);

			// Show the message
			JOptionPane.showMessageDialog(null, message, "Country Status", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void updateInfection() {
		if (infected && normalPopulation > 0) {
			// Calculate new infections based on exponential growth
			int newInfections = (int) Math.ceil(infectedPopulation * infectionRate);
			newInfections = Math.min(newInfections, normalPopulation);

			// Update populations safely
			normalPopulation -= newInfections;
			infectedPopulation += newInfections;

//			// Log output (use logging frameworks for production)
//			System.out.println("New infections: " + newInfections + ", Normal: " + normalPopulation);
//			System.out.println("Infected population: " + infectedPopulation);
		}
	}

	public void updateVaccination() {
		if (vaccinated && (normalPopulation > 0 || infectedPopulation > 0)) {
			double randomRate = 1 + (Math.random() * 2);
			// Calculate new vaccinations with a fixed rate
			int newVaccinations = (int) Math.ceil(vaccinatedPopulation * randomRate);
			newVaccinations = Math.min(newVaccinations, normalPopulation + infectedPopulation);

			// Allocate vaccinations to infected and normal populations
			int vaccinatableFromInfected = Math.min(newVaccinations, infectedPopulation);
			infectedPopulation -= vaccinatableFromInfected;
			vaccinatedPopulation += vaccinatableFromInfected;

			int vaccinatableFromNormal = Math.min(newVaccinations - vaccinatableFromInfected, normalPopulation);
			normalPopulation -= vaccinatableFromNormal;
			vaccinatedPopulation += vaccinatableFromNormal;

			System.out.println("Infected remaining: " + infectedPopulation);
			System.out.println("Normal remaining: " + normalPopulation);
			System.out.println("Vaccinated total: " + vaccinatedPopulation);
		}
	}

	public void setInfected(boolean infected) {
		this.infected = infected;
		if (infected && infectedPopulation == 0) {
			infectedPopulation = 1; // Start with at least one infected individual
		}
		updateButtonAppearance();
	}

	public void setVaccinated(boolean vaccinated) {
		this.vaccinated = vaccinated;
		if (vaccinated && vaccinatedPopulation == 0) {
			vaccinatedPopulation = 1; // Start with at least one vaccinated individual
		}
		updateButtonAppearance();
	}

	private void updateButtonAppearance() {
		double infectionPercentage = (double) infectedPopulation / normalPopulation * 100;
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
			double infectionPercentage = (double) infectedPopulation / getTotalPopulation() * 100;
			return name + " (" + (int) infectionPercentage + "% Infected)";
		} else {
			return name + " (Population: " + normalPopulation + ")";
		}
	}

	public boolean isInfected() {
		return infected;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
		updateButtonAppearance(); // Update button visuals if needed
	}

	public double getInfectionRate() {
		return infectionRate;
	}

	public double getPopulationDensity() {
		return normalPopulation / area;
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

	public int getNormalPopulation() {
		return normalPopulation;
	}

	public int getInfectedPopulation() {
		return infectedPopulation;
	}

	public int getVaccinatedPopulation() {
		return vaccinatedPopulation;
	}

	public int getTotalPopulation() {
		return normalPopulation + infectedPopulation + vaccinatedPopulation;
	}

	public boolean isAllInfected() {
		return infectedPopulation == getTotalPopulation();
	}

	public boolean isNoInfectionsLeft() {
		return infectedPopulation == 0;
	}
}
