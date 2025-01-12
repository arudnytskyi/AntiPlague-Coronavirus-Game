package utilities;

import javax.swing.*;
import java.awt.*;

public class Transport {
	private String type; // "Airline", "Ship", "Train"
	private Country origin;
	private Country destination;
	private JLabel transportIcon;
	private Timer animationTimer;

	public Transport(String type, Country origin, Country destination, JPanel mapPanel) {
		this.type = type;
		this.origin = origin;
		this.destination = destination;

		// Create transport icon
		transportIcon = new JLabel(new ImageIcon("images/" + type.toLowerCase() + ".png")); // Ensure the icons exist
		transportIcon.setBounds(origin.getX(), origin.getY(), 40, 40);
		mapPanel.add(transportIcon);
		mapPanel.revalidate();
		mapPanel.repaint();

		// Animation logic
		animationTimer = new Timer(30, e -> moveIcon());
	}

	public void startTransport() {
		animationTimer.start();
	}

	private void moveIcon() {
		Point currentPos = transportIcon.getLocation();
		Point targetPos = new Point(destination.getX(), destination.getY());

		// Calculate the distance and direction
		int dx = (int) ((targetPos.x - currentPos.x) * 0.05);
		int dy = (int) ((targetPos.y - currentPos.y) * 0.05);

		if (Math.abs(currentPos.x - targetPos.x) < 5 && Math.abs(currentPos.y - targetPos.y) < 5) {
			// Stop animation when destination is reached
			transportIcon.setLocation(targetPos);
			animationTimer.stop();
			spreadInfection();
		} else {
			// Update position
			transportIcon.setLocation(currentPos.x + dx, currentPos.y + dy);
		}
	}

	private void spreadInfection() {
		if (origin.isInfected() && !destination.isInfected()) {
			destination.setInfected(true);
		}
	}
}
