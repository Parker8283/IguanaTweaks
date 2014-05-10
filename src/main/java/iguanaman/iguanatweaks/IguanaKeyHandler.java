package iguanaman.iguanatweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class IguanaKeyHandler {

	@SubscribeEvent
	public void onkeyInput(KeyInputEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen == null)
		{
			EntityClientPlayerMP player = mc.thePlayer;
			NBTTagCompound tags = player.getEntityData();
			tags.removeTag("HideHotbarDelay");
		}
	}
}
