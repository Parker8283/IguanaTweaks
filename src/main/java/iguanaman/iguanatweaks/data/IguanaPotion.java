package iguanaman.iguanatweaks.data;

import iguanaman.iguanatweaks.IguanaTweaks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

public class IguanaPotion extends Potion {

	public IguanaPotion(int par1, boolean par2, int par3) {
		super(par1, par2, par3);
		if(this == IguanaTweaks.poisonNew) {
			setIconIndex(6, 0);
			setEffectiveness(0.25D);
		}else if(this == IguanaTweaks.slowdownNew) {
			setIconIndex(1, 0);
			setPotionName("potion.newSlowdownPotion");
		}
	}

	@Override
	public void performEffect(EntityLivingBase par1EntityLivingBase, int par2)
	{
		if(this == IguanaTweaks.poisonNew)
			par1EntityLivingBase.attackEntityFrom(DamageSource.magic, 1);
	}

}
