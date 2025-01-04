package utilities;

public class Upgrade {
	private String name;
	private int cost;
	private String description;
	private UpgradeEffect effect;

	public Upgrade(String name, int cost, String description, UpgradeEffect effect) {
		this.name = name;
		this.cost = cost;
		this.description = description;
		this.effect = effect;
	}

	public String getName() {
		return name;
	}

	public int getCost() {
		return cost;
	}

	public String getDescription() {
		return description;
	}

	public UpgradeEffect getEffect() {
		return effect;
	}

	public interface UpgradeEffect {
		void apply();
	}
}
