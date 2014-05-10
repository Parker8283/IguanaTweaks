package iguanaman.iguanatweaks;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.material.Material;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.src.ModLoader;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.lwjgl.input.Keyboard;
import org.modstats.ModstatInfo;
import org.modstats.Modstats;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid="IguanaTweaks", name="Iguana Tweaks", version="1.6.X-1i", dependencies = "after:*")
@NetworkMod(clientSideRequired=true, serverSideRequired=true, channels={"IguanaTweaks"}, packetHandler = IguanaPacketHandler.class)
@ModstatInfo(prefix="igtweaks")
public class IguanaTweaks {
	
        // The instance of your mod that Forge uses.
        @Instance("IguanaTweaks")
        public static IguanaTweaks instance;
       
        // Says where the client and server 'proxy' code is loaded.
        @SidedProxy(clientSide="iguanaman.iguanatweaks.ClientProxy", serverSide="iguanaman.iguanatweaks.CommonProxy")
        public static CommonProxy proxy;
        
        public static Potion poisonNew;
        public static Potion slowdownNew;

        public static ConcurrentHashMap<UUID, EntityData> entityDataMap = new ConcurrentHashMap<UUID, EntityData>();

        @EventHandler
        public void preInit(FMLPreInitializationEvent event) {
        	
        	IguanaConfig.Init(event.getSuggestedConfigurationFile());
        	
        	slowdownNew = new Potion(IguanaConfig.damageSlowdownPotionId, true, 5926017).setIconIndex(1, 0).setPotionName("potion.newSlowdownPotion");
            proxy.registerLocalization();

            
            // LESS OBVIOUS SILVERFISH
            if (IguanaConfig.lessObviousSilverfish)
            {
	        	IguanaLog.log("Hiding silverfish");
	            Block.silverfish.setHardness(1.5f).setResistance(10.0F).setStepSound(Block.soundStoneFootstep);
	            MinecraftForge.setBlockHarvestLevel(Block.silverfish, "pickaxe", 0);
            }
            
            
            // CUSTOM POISON EFFECT
            if (IguanaConfig.alterPoison)
            {
            	IguanaLog.log("Altering poison");
                Potion.potionTypes[19] = null;
                poisonNew = (new IguanaPotion(19, true, 5149489)).setPotionName("potion.poison").setIconIndex(6, 0).setEffectiveness(0.25D);
            }

        }

        @EventHandler
        public void load(FMLInitializationEvent event) {
                Modstats.instance().getReporter().registerMod(this);
        		
        		if (IguanaConfig.torchesPerCoal != 4) {
        			IguanaLog.log("Changing torch recipe output");
        			RecipeRemover.removeAnyRecipe(new ItemStack(Block.torchWood, 4));
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.torchWood, IguanaConfig.torchesPerCoal), 
                    		"c", "s", 'c', new ItemStack(Item.coal), 's', new ItemStack(Item.stick)));
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.torchWood, IguanaConfig.torchesPerCoal), 
                    		"c", "s", 'c', new ItemStack(Item.coal, 1, 1), 's', new ItemStack(Item.stick)));
        		}
        }

        @EventHandler
        public void postInit(FMLPostInitializationEvent event) {
    		
    		// STACK SIZE REDUCTION
        	StackSizeTweaks.init();
        	
        	// FURNACE XP TWEAKS
        	XpTweaks.init();
        	
        	if (IguanaConfig.hideExperience) proxy.disableExperienceHud();
        	
			if (IguanaConfig.maxCarryWeight > 0) IguanaLog.log("Starting weight watcher");
			
           MinecraftForge.EVENT_BUS.register(new IguanaEventHook());
           GameRegistry.registerPlayerTracker(new IguanaPlayerHandler());
           proxy.registerTickHandler();
        }
		
		public static double getBlockWeight(Block block)
		{
			Material blockMaterial = block.blockMaterial;
        	
	        if (blockMaterial == Material.iron || blockMaterial == Material.anvil) return 1.5d;
	        else if (blockMaterial == Material.rock) return 1d;
	        else if (blockMaterial == Material.grass || blockMaterial == Material.ground 
	        		|| blockMaterial == Material.sand || blockMaterial == Material.snow 
	        		|| blockMaterial == Material.wood || blockMaterial == Material.glass 
	        		|| blockMaterial == Material.ice || blockMaterial == Material.tnt) return 0.5d;
	        else if (blockMaterial == Material.cloth) return 0.25d;
	        else if (block.isOpaqueCube()) return 1d / 16d;
	        else return 1d / 64d; // item like block
		}

}