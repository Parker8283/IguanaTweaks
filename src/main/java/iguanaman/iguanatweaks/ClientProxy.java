package iguanaman.iguanatweaks;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
       
        @Override
        public void registerRenderers() {}
        
        public void registerTickHandler() {
            TickRegistry.registerTickHandler(new IguanaTickHandler(), Side.CLIENT);
        }
        
        public void registerLocalization() {
        	LanguageRegistry.instance().addStringLocalization("potion.newSlowdownPotion", "In Pain");
        }
        
        public void disableExperienceHud()
        {
        	GuiIngameForge.renderExperiance = false;
        }
       
}