package iguanaman.iguanatweaks;

import iguanaman.iguanatweaks.data.IguanaPotion;
import iguanaman.iguanatweaks.events.IguanaEventHook;
import iguanaman.iguanatweaks.events.IguanaKeyHandler;
import iguanaman.iguanatweaks.events.IguanaPlayerHandler;
import iguanaman.iguanatweaks.events.IguanaTickHandler;
import iguanaman.iguanatweaks.proxy.CommonProxy;
import iguanaman.iguanatweaks.util.RecipeRemover;
import iguanaman.iguanatweaks.util.StackSizeTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="IguanaTweaks", name="Iguana Tweaks", version="@MOD_VERSION@", dependencies = "required-after:Forge@[10.12.2.1121,);")
public class IguanaTweaks {

	// The instance of your mod that Forge uses.
	@Instance("IguanaTweaks")
	public static IguanaTweaks instance;

	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide="iguanaman.iguanatweaks.proxy.ClientProxy", serverSide="iguanaman.iguanatweaks.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static Logger log;

	public static Potion poisonNew;
	public static Potion slowdownNew;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		log = event.getModLog();

		IguanaConfig.Init(event.getSuggestedConfigurationFile());

		slowdownNew = new IguanaPotion(IguanaConfig.damageSlowdownPotionId, true, 5926017);

		// LESS OBVIOUS SILVERFISH
		if (IguanaConfig.lessObviousSilverfish) {
			log.info("Hiding silverfish");
			Blocks.monster_egg.setHardness(1.5f).setResistance(10.0F).setStepSound(Block.soundTypeStone);
			Blocks.monster_egg.setHarvestLevel("pickaxe", 0);
		}


		// CUSTOM POISON EFFECT
		if (IguanaConfig.alterPoison)
		{
			log.info("Altering poison");
			Potion.potionTypes[19] = null;
			poisonNew = (new IguanaPotion(19, true, 5149489)).setPotionName("potion.poison");
		}

	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		if (IguanaConfig.torchesPerCoal != 4) {
			log.info("Changing torch recipe output");
			RecipeRemover.removeAnyRecipe(new ItemStack(Blocks.torch, 4));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.torch, IguanaConfig.torchesPerCoal), 
					"c", "s", 'c', new ItemStack(Items.coal), 's', new ItemStack(Items.stick)));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.torch, IguanaConfig.torchesPerCoal), 
					"c", "s", 'c', new ItemStack(Items.coal, 1, 1), 's', new ItemStack(Items.stick)));
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		// STACK SIZE REDUCTION
		StackSizeTweaks.init();

		if (IguanaConfig.hideExperience) proxy.disableExperienceHud();

		if (IguanaConfig.maxCarryWeight > 0) log.info("Starting weight watcher");

		MinecraftForge.EVENT_BUS.register(new IguanaEventHook());
		FMLCommonHandler.instance().bus().register(new IguanaPlayerHandler());
		FMLCommonHandler.instance().bus().register(new IguanaTickHandler());
		FMLCommonHandler.instance().bus().register(new IguanaKeyHandler());
	}

	public static double getBlockWeight(Block block)
	{
		Material blockMaterial = block.getMaterial();

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