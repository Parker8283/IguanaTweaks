package iguanaman.iguanatweaks;

import net.minecraftforge.client.GuiIngameForge;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderers() {}

	public void registerLocalization() {
		LanguageRegistry.instance().addStringLocalization("potion.newSlowdownPotion", "In Pain");
	}

	public void disableExperienceHud()
	{
		GuiIngameForge.renderExperiance = false;
	}
}