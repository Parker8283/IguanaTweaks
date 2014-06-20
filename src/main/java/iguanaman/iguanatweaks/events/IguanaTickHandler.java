package iguanaman.iguanatweaks.events;

import iguanaman.iguanatweaks.IguanaTweaks;
import iguanaman.iguanatweaks.config.IguanaConfig;
import iguanaman.iguanatweaks.data.EntityData;

import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class IguanaTickHandler {

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

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if(event.phase == Phase.START) {
			keyTick(false);

			for(Map.Entry<UUID, EntityData> entry : IguanaTweaks.entityDataMap.entrySet())
			{
				EntityData data = entry.getValue();
				if (++data.age >= IguanaConfig.tickRateEntityUpdate)
					IguanaTweaks.entityDataMap.remove(entry.getKey());
			}
		}else{
			keyTick(true);
		}
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
}