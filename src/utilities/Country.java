package utilities;

import javax.swing.*;
import java.awt.*;

public class Country {
	private final String name;
	private final String continent;
	private final JButton button;
	private final int x, y;
	private double infectionRate;
	private double infectionResistance;
	private final double area;
	private boolean selectable = false;
	private boolean infected = false;
	private boolean vaccinated = false;
	private int normalPopulation;
	private int infectedPopulation;
	private int vaccinatedPopulation;

	public Country(String name, int x, int y, String continent, double infectionResistance, int totalPopulation, double area) {
		this.name = name;
		this.continent = continent;
		this.area = area;
		this.x = x;
		this.y = y;
		this.infectionResistance = infectionResistance;
		this.normalPopulation = totalPopulation;
		this.infectedPopulation = 0;
		this.vaccinatedPopulation = 0;

		button = new JButton(name);
		button.setOpaque(true);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);
		button.setContentAreaFilled(true);
		button.setBounds(x, y, 100, 35);
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
			int totalPopulation = getTotalPopulation();
			String message = getCountryStatus(totalPopulation);

			JOptionPane.showMessageDialog(null, message, "Country Status", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private String getCountryStatus(int totalPopulation) {
		double normalPercentage = ((double) normalPopulation / totalPopulation) * 100;
		double infectedPercentage = ((double) infectedPopulation / totalPopulation) * 100;
		double vaccinatedPercentage = ((double) vaccinatedPopulation / totalPopulation) * 100;

		return String.format(
				"%s Infection Rate: %.2f%% %n" +
				"Infected Population: %d (%.2f%%)%n" +
				"Normal Population: %d (%.2f%%)%n" +
				"Vaccinated Population: %d (%.2f%%)%n",
				name, infectionRate + infectionResistance,
				infectedPopulation, infectedPercentage,
				normalPopulation, normalPercentage,
				vaccinatedPopulation, vaccinatedPercentage
		);

	}

	public void updateInfection() {
		if (infected && normalPopulation > 0) {
			int newInfections = (int) Math.ceil(infectedPopulation * (infectionRate+infectionResistance));
			newInfections = Math.min(newInfections, normalPopulation);

			normalPopulation -= newInfections;
			infectedPopulation += newInfections;
		}
	}

	public void updateVaccination() {
		if (vaccinated && (normalPopulation > 0 || infectedPopulation > 0)) {
			double randomRate = 1 + (Math.random() * 2);

			int newVaccinations = (int) Math.ceil(vaccinatedPopulation * randomRate);
			newVaccinations = Math.min(newVaccinations, normalPopulation + infectedPopulation);

			int vaccinatableFromInfected = Math.min(newVaccinations, infectedPopulation);
			infectedPopulation -= vaccinatableFromInfected;
			vaccinatedPopulation += vaccinatableFromInfected;

			int vaccinatableFromNormal = Math.min(newVaccinations - vaccinatableFromInfected, normalPopulation);
			normalPopulation -= vaccinatableFromNormal;
			vaccinatedPopulation += vaccinatableFromNormal;
		}
	}

	public void setInfected(boolean infected) {
		this.infected = infected;
		if (infected && infectedPopulation == 0) {
			infectedPopulation = 1;
		}
		updateButtonAppearance();
	}

	public void setVaccinated(boolean vaccinated) {
		this.vaccinated = vaccinated;
		if (vaccinated && vaccinatedPopulation == 0) {
			vaccinatedPopulation = 1;
		}
		updateButtonAppearance();
	}

	private void updateButtonAppearance() {
		if (vaccinated) {
			button.setBackground(new Color(144,213,255));
		} else if (infected) {
			button.setBackground(new Color(255, 150, 150));
		} else {
			button.setBackground(Color.WHITE);
		}
		button.setForeground(Color.BLACK);
		button.repaint();
	}

	public boolean isInfected() {
		return infected;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
		updateButtonAppearance();
	}

	public void setInfectionRate(double infectionRate) {
		this.infectionRate = infectionRate;
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

	public boolean isVaccinated() {
		return vaccinated;
	}

	public int getNormalPopulation() {
		return normalPopulation;
	}

	public int getInfectedPopulation() {
		return infectedPopulation;
	}

	public int getTotalPopulation() {
		return normalPopulation + infectedPopulation + vaccinatedPopulation;
	}

	public boolean isAllInfected() {
		return infectedPopulation == getTotalPopulation();
	}
}
