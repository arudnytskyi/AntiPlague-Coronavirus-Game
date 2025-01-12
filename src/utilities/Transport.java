package utilities;

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
	private ImageIcon originalIcon;

	public Transport(String type, Country origin, Country destination, JPanel mapPanel) {
		this.type = type;
		this.origin = origin;
		this.destination = destination;
		this.mapPanel = mapPanel;

		// Load and scale the original icon
		originalIcon = scaleIcon(new ImageIcon("images/" + type.toLowerCase() + ".png"), ICON_SIZE, ICON_SIZE);
		transportIcon = new JLabel(originalIcon);
		transportIcon.setBounds(origin.getX(), origin.getY(), ICON_SIZE, ICON_SIZE);
		transportIcon.setVisible(false); // Initially hidden
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

	public void startTransport() {
		// Calculate distance and duration
		int dx = destination.getX() - origin.getX();
		int dy = destination.getY() - origin.getY();
		double distance = Math.sqrt(dx * dx + dy * dy);

		totalSteps = (int) (distance * 2);
		currentStep = 0;

		stepX = (double) dx / totalSteps;
		stepY = (double) dy / totalSteps;

		// Calculate angle for rotation (in radians)
		double angle = Math.atan2(dy, dx);

		// Set rotated icon and initial position
		transportIcon.setIcon(rotateIcon(originalIcon, angle));
		transportIcon.setLocation(origin.getX(), origin.getY());
		transportIcon.setVisible(true);

		// Start the animation timer
		animationTimer = new Timer(10, e -> animateTransport());
		animationTimer.start();
	}

	private void animateTransport() {
		currentStep++;
		int newX = (int) (origin.getX() + stepX * currentStep);
		int newY = (int) (origin.getY() + stepY * currentStep);
		transportIcon.setLocation(newX, newY);

		mapPanel.revalidate();
		mapPanel.repaint();

		if (currentStep >= totalSteps) {
			stopAnimation();
		}
	}

	private void stopAnimation() {
		if (animationTimer != null) {
			animationTimer.stop();
			animationTimer = null;
		}

		transportIcon.setLocation(destination.getX(), destination.getY());
		transportIcon.setVisible(false);
		transportIcon.setLocation(origin.getX(), origin.getY());
		mapPanel.revalidate();
		mapPanel.repaint();

		spreadInfection();
	}

	private void spreadInfection() {
		if (origin.isInfected() && !destination.isInfected()) {
			destination.setInfected(true);
		}
	}
}