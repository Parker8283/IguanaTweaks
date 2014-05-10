package iguanaman.iguanatweaks;

public class EntityData {

	public double currentWeight;
	public double maxWeight;
	public double encumberance;
	public double armour;
	public double speedModifier;
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
