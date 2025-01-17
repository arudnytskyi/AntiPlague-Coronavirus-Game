package utilities;

import windows.GameWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Transport {
	private final String type; // "Airline", "Ship", "Train"
	private final Country origin;
	private final Country destination;
	private final JLabel transportIcon;
	private final JPanel mapPanel;
	private final ImageIcon normalIcon;
	private final ImageIcon infectedIcon;
	private final ImageIcon vaccineIcon;
	private static final int ICON_SIZE = 30;
	private Timer animationTimer;
	private int totalSteps;
	private int currentStep;
	private double stepX, stepY;
	private static double sanitationEffect = 1.0;
	private static boolean rapidTestingEnabled = false;
	private static boolean vaccinePriority = false;

	public Transport(String type, Country origin, Country destination, JPanel mapPanel) {
		this.type = type;
		this.origin = origin;
		this.destination = destination;
		this.mapPanel = mapPanel;

		// Load and scale the original icon
		normalIcon = scaleIcon(new ImageIcon("images/" + type.toLowerCase() + ".png"));
		infectedIcon = scaleIcon(new ImageIcon("images/" + type.toLowerCase() + "_infected.png"));
		vaccineIcon = scaleIcon(new ImageIcon("images/" + type.toLowerCase() + "_vaccine.png"));

		transportIcon = new JLabel(normalIcon);
		transportIcon.setBounds(origin.getX(), origin.getY(), ICON_SIZE, ICON_SIZE);
		transportIcon.setVisible(false);
		mapPanel.add(transportIcon);
		mapPanel.revalidate();
		mapPanel.repaint();
	}

	private ImageIcon scaleIcon(ImageIcon originalIcon) {
		Image scaledImage = originalIcon.getImage().getScaledInstance(Transport.ICON_SIZE, Transport.ICON_SIZE, Image.SCALE_SMOOTH);
		return new ImageIcon(scaledImage);
	}

	private ImageIcon rotateIcon(ImageIcon icon, double angle) {
		Image originalImage = icon.getImage();
		int w = originalImage.getWidth(null);
		int h = originalImage.getHeight(null);

		BufferedImage rotatedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = rotatedImage.createGraphics();

		AffineTransform transform = new AffineTransform();
		transform.translate(w / 2.0, h / 2.0);
		transform.rotate(angle);
		transform.translate(-w / 2.0, -h / 2.0);

		g2d.setTransform(transform);
		g2d.drawImage(originalImage, 0, 0, null);
		g2d.dispose();

		return new ImageIcon(rotatedImage);
	}

	public void startTransport(boolean forVaccine) {
		int dx = destination.getX() - origin.getX();
		int dy = destination.getY() - origin.getY();
		double distance = Math.sqrt(dx * dx + dy * dy);

		totalSteps = (int) (distance * 2);
		currentStep = 0;

		stepX = (double) dx / totalSteps;
		stepY = (double) dy / totalSteps;

		double infectionProbability = (double) origin.getInfectedPopulation() / origin.getNormalPopulation() * sanitationEffect;;
		boolean isInfected = Math.random() < infectionProbability;

		double angle = Math.atan2(dy, dx);
		ImageIcon icon = forVaccine ? vaccineIcon : (isInfected ? infectedIcon : normalIcon);

		transportIcon.setIcon(rotateIcon(icon, angle));
		transportIcon.setLocation(origin.getX(), origin.getY());
		transportIcon.setVisible(true);

		animationTimer = new Timer(10, e -> animateTransport(isInfected, forVaccine));
		animationTimer.start();
	}
	private void spreadInfection() {
		if (origin.isInfected() && !destination.isInfected()) {
			destination.setInfected(true);
			destination.updateInfection();

			JOptionPane.showMessageDialog(
					null,
					"infection has spread to " + destination.getName(),
					"Infection Update",
					JOptionPane.WARNING_MESSAGE
			);
		}
	}

	public void spreadVaccine() {
		if (!destination.isVaccinated()) {
			destination.setVaccinated(true);
			destination.updateVaccination();

			JOptionPane.showMessageDialog(
					null,
					"Vaccine has spread to " + destination.getName(),
					"Vaccine Update",
					JOptionPane.INFORMATION_MESSAGE
			);
		}
	}

	private void animateTransport(boolean isInfected, boolean forVaccine) {
		currentStep++;
		int newX = (int) (origin.getX() + stepX * currentStep);
		int newY = (int) (origin.getY() + stepY * currentStep);
		transportIcon.setLocation(newX, newY);

		mapPanel.revalidate();
		mapPanel.repaint();

		if (currentStep >= totalSteps) {
			stopAnimation(isInfected, forVaccine);
		}
	}

	public boolean isRouteOperational() {
		if (GameWindow.getGlobalAwareness() >= 70 && !vaccinePriority) {
			return false;
		}

		if (vaccinePriority) return true;

		double originInfection = (double) origin.getInfectedPopulation() / origin.getNormalPopulation();
		double destinationInfection = (double) destination.getInfectedPopulation() / destination.getNormalPopulation();

		// Infection Level Restriction with Rapid Testing
		if (type.equals("Airline") && (originInfection > 0.2 || destinationInfection > 0.2)) {
			if (rapidTestingEnabled) {
				return originInfection <= 0.25 && destinationInfection <= 0.25; // Relaxed condition for reopening
			}
			return false;
		}

		// Population Density Restriction
		if (type.equals("Train") && (origin.getPopulationDensity() > 500 || destination.getPopulationDensity() > 500)) return false;

		// Proximity Restriction
		if (type.equals("Bus") && !origin.getContinent().equals(destination.getContinent())) return false;

		// Global Awareness Restriction
		if (GameWindow.getGlobalAwareness() > 70 && !type.equals("Vaccine")) return false;

		return true;
	}

	private void stopAnimation(boolean isInfected, boolean forVaccine) {
		if (animationTimer != null) {
			animationTimer.stop();
			animationTimer = null;
		}

		transportIcon.setVisible(false);
		transportIcon.setLocation(origin.getX(), origin.getY());
		mapPanel.revalidate();
		mapPanel.repaint();

		if (forVaccine) {
			spreadVaccine();
		} else if (isInfected) {
			spreadInfection();
		}
	}

	public void stopAnimationManually() {
		if (animationTimer != null) {
			animationTimer.stop();
			animationTimer = null;
		}

		transportIcon.setVisible(false);
		transportIcon.setLocation(origin.getX(), origin.getY());
		mapPanel.revalidate();
		mapPanel.repaint();
	}

	public static void setSanitationEffect(double effect) {
		sanitationEffect = effect;
	}

	public static void setRapidTesting(boolean enabled) {
		rapidTestingEnabled = enabled;
	}

	public static void setVaccinePriority(boolean enabled) {
		vaccinePriority = enabled;
	}
}