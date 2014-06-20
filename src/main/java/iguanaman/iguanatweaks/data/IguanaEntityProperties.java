package iguanaman.iguanatweaks.data;

import iguanaman.iguanatweaks.util.IguanaUtils;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class IguanaEntityProperties implements IExtendedEntityProperties {
	
	public double speedModifier, currentWeight, maxWeight, encumberance, armour;
	public int age;

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound tags = new NBTTagCompound();
		tags.setDouble("speedModifier", speedModifier);
		tags.setDouble("currentWeight", currentWeight);
		tags.setDouble("maxWeight", maxWeight);
		tags.setDouble("encumberance", encumberance);
		tags.setDouble("armour", armour);
		tags.setInteger("age", age);
		compound.setTag("IguanaEntityProperties", tags);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound tags = compound.getCompoundTag("IguanaEntityProperties");
		this.speedModifier = tags.getDouble("speedModifier");
		this.currentWeight = tags.getDouble("currentWeight");
		this.maxWeight = tags.getDouble("maxWeight");
		this.encumberance = tags.getDouble("encumberance");
		this.age = tags.getInteger("age");
		this.armour = tags.getDouble("armour");
	}

	@Override
	public void init(Entity entity, World world) {
		if (speedModifier != 1d && !IguanaUtils.isJumping(entity)) {
			speedModifier = (2d * speedModifier) - 1d;
			entity.motionX *= speedModifier;
			entity.motionZ *= speedModifier;
		}
	}
	
	public void setAll(double speedModifier, double currentWeight, double maxWeight, double encumberance, double armour) {
		this.speedModifier = speedModifier;
		this.currentWeight = currentWeight;
		this.maxWeight = maxWeight;
		this.encumberance = encumberance;
		this.armour = armour;
		this.age = 0;
	}
	
	public void zeroAll() {
		this.speedModifier = 0;
		this.currentWeight = 0;
		this.maxWeight = 0;
		this.encumberance = 0;
		this.armour = 0;
	}
}
