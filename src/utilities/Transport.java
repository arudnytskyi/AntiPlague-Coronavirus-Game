package utilities;

import windows.GameWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Transport {
	private String type; // "Airline", "Ship", "Train"
	private Country origin;
	private Country destination;
	private JLabel transportIcon;
	private Timer animationTimer;
	private JPanel mapPanel;
	private int totalSteps;
	private int currentStep;
	private double stepX, stepY;
	private static final int ICON_SIZE = 30; // Desired icon size (width and height)
	private ImageIcon normalIcon;
	private ImageIcon infectedIcon;
	private ImageIcon vaccineIcon;
	private static double sanitationEffect = 1.0;
	private static boolean rapidTestingEnabled = false;
	private static boolean vaccinePriority = false;

	public Transport(String type, Country origin, Country destination, JPanel mapPanel) {
		this.type = type;
		this.origin = origin;
		this.destination = destination;
		this.mapPanel = mapPanel;

		// Load and scale the original icon
		normalIcon = scaleIcon(new ImageIcon("images/" + type.toLowerCase() + ".png"), ICON_SIZE, ICON_SIZE);
		infectedIcon = scaleIcon(new ImageIcon("images/" + type.toLowerCase() + "_infected.png"), ICON_SIZE, ICON_SIZE);
		vaccineIcon = scaleIcon(new ImageIcon("images/" + type.toLowerCase() + "_vaccine.png"), ICON_SIZE, ICON_SIZE);

		transportIcon = new JLabel(normalIcon);
		transportIcon.setBounds(origin.getX(), origin.getY(), ICON_SIZE, ICON_SIZE);
		transportIcon.setVisible(false);
		mapPanel.add(transportIcon);
		mapPanel.revalidate();
		mapPanel.repaint();
	}

	private ImageIcon scaleIcon(ImageIcon originalIcon, int width, int height) {
		// Scale the image to fit within the specified dimensions
		Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(scaledImage);
	}

	private ImageIcon rotateIcon(ImageIcon icon, double angle) {
		// Rotate the icon image
		Image originalImage = icon.getImage();
		int w = originalImage.getWidth(null);
		int h = originalImage.getHeight(null);

		BufferedImage rotatedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = rotatedImage.createGraphics();

		// Apply rotation around the center of the image
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
		// Calculate distance and duration
		int dx = destination.getX() - origin.getX();
		int dy = destination.getY() - origin.getY();
		double distance = Math.sqrt(dx * dx + dy * dy);

		totalSteps = (int) (distance * 2);
		currentStep = 0;

		stepX = (double) dx / totalSteps;
		stepY = (double) dy / totalSteps;

		// Determine if the transport is infected
		double infectionProbability = (double) origin.getInfectedPopulation() / origin.getNormalPopulation() * sanitationEffect;;
		boolean isInfected = Math.random() < infectionProbability;

		// Calculate angle for rotation (in radians)
		double angle = Math.atan2(dy, dx);
		ImageIcon icon = forVaccine ? vaccineIcon : (isInfected ? infectedIcon : normalIcon);

		// Set initial position and visibility
		transportIcon.setIcon(rotateIcon(icon, angle));
		transportIcon.setLocation(origin.getX(), origin.getY());
		transportIcon.setVisible(true);

		// Start the animation timer
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
		if (GameWindow.getGlobalAwareness() >= 70) {
			if (!vaccinePriority) {
				return false; // All routes are closed unless vaccine priority is enabled
			}
		}

		double originInfection = (double) origin.getInfectedPopulation() / origin.getNormalPopulation();
		double destinationInfection = (double) destination.getInfectedPopulation() / destination.getNormalPopulation();

		if (vaccinePriority) {
			return true;
		}

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

		// Hide the transport icon and reset its position
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

	public String getType() {
		return this.type;
	}
}