package iguanaman.iguanatweaks;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

public class IguanaPotion extends Potion {

	public IguanaPotion(int par1, boolean par2, int par3) {
		super(par1, par2, par3);
	}
	
	@Override
    public void performEffect(EntityLivingBase par1EntityLivingBase, int par2)
    {
		par1EntityLivingBase.attackEntityFrom(DamageSource.magic, 1);
    }

}
