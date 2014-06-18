package iguanaman.iguanatweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

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
	public void onRenderTick(WorldTickEvent event) {
		if(event.phase == Phase.START) {
			keyTick(false);

			for(WorldServer server : MinecraftServer.getServer().worldServers) {
				for(int i = 0; i < server.loadedEntityList.size(); i++) {
					Entity entity = (Entity)server.loadedEntityList.get(i);
					if(entity instanceof EntityLivingBase) {
						EntityLivingBase elb = (EntityLivingBase)entity;
						if(elb.getExtendedProperties("IguanaEntityProperties") != null) {
							IguanaEntityProperties props = (IguanaEntityProperties)elb.getExtendedProperties("IguanaEntityProperties");
							if(++props.age >= IguanaConfig.tickRateEntityUpdate) {
								props.zeroAll();
							}
						}
					}
				}
			}
		} else {
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
