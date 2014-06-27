package iguanaman.iguanatweaks.util;

import java.lang.reflect.Field;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import cpw.mods.fml.relauncher.ReflectionHelper;
import iguanaman.iguanatweaks.IguanaTweaks;

public class IguanaUtils {

    public static boolean isJumping(Entity entity) {
        boolean jumping = false;
        Field jumpField = null;
        try {
            jumpField = ReflectionHelper.findField(EntityLivingBase.class, "jumpTicks", "field_70773_bE", "bq");
            jumpField.setAccessible(true);
        } catch(Exception e) {
            IguanaTweaks.log.fatal("Could not access jumpTick in net.minecraft.entity.EntityLivingBase.");
        }

        if(jumpField != null) {
            try {
                if(jumpField.getInt(entity) > 0)
                    jumping = true;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return jumping;
    }
}
