package iguanaman.iguanatweaks;

public class EntityData {

	public double currentWeight = 0;
	public double maxWeight = 0;
	public double encumberance = 0;
	public double armour = 0;
	public double speedModifier = 0;
	public int age = 0;

	public EntityData(double speedModifier, double currentWeight, double maxWeight, double encumberance, double armour) {
		this.speedModifier = speedModifier;
		this.currentWeight = currentWeight;
		this.maxWeight = maxWeight;
		this.encumberance = encumberance;
		this.armour = armour;
		this.age = 0;
	}

}
