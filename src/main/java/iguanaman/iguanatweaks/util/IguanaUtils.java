package iguanaman.iguanatweaks.util;

import net.minecraft.entity.EntityLivingBase;

public class IguanaUtils {

    public static boolean isJumping(EntityLivingBase entity) {
        return entity.jumpTicks > 0;
    }
}
