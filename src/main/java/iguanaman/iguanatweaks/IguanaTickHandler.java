package iguanaman.iguanatweaks;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class IguanaTickHandler implements ITickHandler {
	
	public static int[] keys = { 
		Keyboard.KEY_0, 
		Keyboard.KEY_1, 
		Keyboard.KEY_2, 
		Keyboard.KEY_3, 
		Keyboard.KEY_4, 
		Keyboard.KEY_5, 
		Keyboard.KEY_6, 
		Keyboard.KEY_7, 
		Keyboard.KEY_8, 
		Keyboard.KEY_9 
	};
	
    protected boolean[] keyDown;

	public IguanaTickHandler() {
        this.keyDown = new boolean[keys.length];
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
        keyTick(false);
        
		Iterator<Map.Entry<UUID, EntityData>> it = IguanaTweaks.entityDataMap.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<UUID, EntityData> entry = it.next();
        	EntityData data = entry.getValue();
        	if (++data.age >= IguanaConfig.tickRateEntityUpdate) it.remove();
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        keyTick(true);
	}
	
    private void keyTick(boolean tickEnd)
    {
        for (int i = 0; i < keys.length; i++)
        {
            boolean state = Keyboard.isKeyDown(keys[i]);
            if (state != keyDown[i])
            {
                if (state)
                {
            		Minecraft mc = Minecraft.getMinecraft();
            		if (mc.currentScreen == null)
            		{
            			EntityClientPlayerMP player = mc.thePlayer;
            			NBTTagCompound tags = player.getEntityData();
            			tags.removeTag("HideHotbarDelay");
            		}
                }
                
                if (tickEnd) keyDown[i] = state;
            }
        }
    }

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
        return "IguanaTickHandler";
	}

}
