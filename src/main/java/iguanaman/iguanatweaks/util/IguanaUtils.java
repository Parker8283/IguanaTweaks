package iguanaman.iguanatweaks.util;

import iguanaman.iguanatweaks.IguanaTweaks;

import java.lang.reflect.Field;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class IguanaUtils {
	
	public static boolean isJumping(Entity entity) {
		boolean jumping = false;
		Field jumpField = null;
		boolean triedField = false;
		if (jumpField == null && !triedField)
		{
			try {
				jumpField = ReflectionHelper.findField(EntityLivingBase.class, "jumpTicks", "field_70773_bE", "bq");
				jumpField.setAccessible(true);
			} catch (Exception e) {
				IguanaTweaks.log.fatal("Could not access jumpTick in net.minecraft.entity.EntityLivingBase.");
			}
			triedField = true;
		}

		if (jumpField != null) {
			try {
				if (jumpField != null) {
					if (jumpField.getInt(entity) > 0) jumping = true;
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return jumping;
	}
}
