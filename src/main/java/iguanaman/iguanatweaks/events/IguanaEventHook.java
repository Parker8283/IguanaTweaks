package iguanaman.iguanatweaks.events;

import java.util.Iterator;

import net.minecraft.block.BlockGravel;
import net.minecraft.init.Blocks;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import iguanaman.iguanatweaks.IguanaTweaks;
import iguanaman.iguanatweaks.config.IguanaConfig;
import iguanaman.iguanatweaks.config.IguanaWeightsConfig;
import iguanaman.iguanatweaks.data.EntityData;
import iguanaman.iguanatweaks.util.IguanaUtils;

public class IguanaEventHook {

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {

        EntityLivingBase entity = event.entityLiving;
        World world = entity.worldObj;

        if(world.isRemote && entity != null) {
            double speedModifier = 1d;

            boolean isCreative = false;

            if(entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)entity;
                if(entity.getAge() % IguanaConfig.tickRateEntityUpdate == 0 && IguanaConfig.increasedStepHeight)
                    player.stepHeight = 1f;
                if(player.capabilities.isCreativeMode)
                    isCreative = true;

                NBTTagCompound tags = player.getEntityData();

                if(IguanaConfig.hideHealthBar) {
                    if(player.getHealth() >= (float)IguanaConfig.hideHealthBarThreshold) {
                        int delay = tags.getInteger("HideHealthBarDelay");
                        if(delay >= IguanaConfig.hideHealthBarDelay * 20) {
                            GuiIngameForge.renderHealth = false;
                        } else {
                            tags.setInteger("HideHealthBarDelay", ++delay);
                        }
                    } else {
                        tags.removeTag("HideHealthBarDelay");
                        GuiIngameForge.renderHealth = true;
                    }
                }

                if(IguanaConfig.hideHungerBar) {
                    if(player.getFoodStats().getFoodLevel() >= IguanaConfig.hideHungerBarThreshold) {
                        int delay = tags.getInteger("HideHungerBarDelay");
                        if(delay >= IguanaConfig.hideHungerBarDelay * 20) {
                            GuiIngameForge.renderFood = false;
                        } else {
                            tags.setInteger("HideHungerBarDelay", ++delay);
                        }
                    } else {
                        tags.removeTag("HideHungerBarDelay");
                        GuiIngameForge.renderFood = true;
                    }
                }

                if(IguanaConfig.hideHotbar) {
                    int delay = tags.getInteger("HideHotbarDelay");
                    if(delay >= IguanaConfig.hideHotbarDelay * 20) {
                        GuiIngameForge.renderHotbar = false;
                    } else {
                        tags.setInteger("HideHotbarDelay", ++delay);
                        GuiIngameForge.renderHotbar = true;
                    }
                }
            }

            if(IguanaTweaks.entityDataMap.containsKey(entity.getUniqueID())) {
                if(IguanaTweaks.entityDataMap.get(entity.getUniqueID()) != null) {
                    speedModifier = IguanaTweaks.entityDataMap.get(entity.getUniqueID()).speedModifier;
                } else {
                    IguanaTweaks.log.error("There's an entry in entityDataMap == null. That's bad.");
                }
            } else {
                double slownessWeight = 0d;
                double slownessArmour = (double)entity.getTotalArmorValue() * IguanaConfig.armorWeight;
                double weight = 0d;
                double maxWeight = 0d;

                if(!isCreative) {
                    int slownessHurt = 0;
                    int slownessTerrain = 0;
                    boolean onIce = false;

                    //Stun effect
                    PotionEffect currentEffect = entity.getActivePotionEffect(IguanaTweaks.slowdownNew);
                    if(currentEffect != null) {
                        slownessHurt += Math.round(100f * ((float)currentEffect.getDuration() / (float)currentEffect.getAmplifier()));
                    }

                    //Walking block
                    if(!entity.isInWater() && IguanaConfig.terrainSlowdownPercentage > 0) {
                        double posmod = 0d;

                        if(entity instanceof EntityClientPlayerMP)
                            posmod = 1.039d;

                        int posX = (int)entity.posX;
                        int posY = (int)(entity.posY - posmod - 1d);
                        int posZ = (int)entity.posZ;
                        int posX1 = 0;
                        int posZ1 = 0;
                        if(posX < 0)
                            --posX;
                        if(posY < 0)
                            --posY;
                        if(posZ < 0)
                            --posZ;

						/*
                        if (entity instanceof EntityPlayer)
							FMLLog.warning("posY" + Double.toString(entity.posY - posmod) + " class" + entity.getClass().toString());
							//FMLLog.warning("posX" + posX + " posY" + posY + " posZ" + posZ);
						 */

                        Material blockOnMaterial = world.getBlock(posX, posY, posZ).getMaterial();
                        Material blockInMaterial = world.getBlock(posX, posY + 1, posZ).getMaterial();
                        Block blockOnBlock = world.getBlock(posX, posY, posZ);
                        Material blockOnMaterial1 = world.getBlock(posX, posY, posZ).getMaterial();
                        if(entity.motionY > -0.1 && blockOnMaterial1 == Material.air) {
                            if (Math.abs(entity.posX - (int) entity.posX) < 0.3) {
                                posX1 = (int) (entity.posX - (1.3 * (entity.posX - (int) entity.posX)));
                                if(posX1 < 0)
                                    --posX1;
                            }
                            if (Math.abs(entity.posX - (int) entity.posX) > 0.7) {
                                posX1 = (int) (entity.posX + (1.3 * (1 - (entity.posX - (int) entity.posX))));
                                if(posX1 < 0)
                                    --posX1;
                            }
                            if (Math.abs(entity.posZ - (int) entity.posZ) < 0.3) {
                                posZ1 = (int) (entity.posZ - (1.3 * (entity.posZ - (int) entity.posZ)));
                                if(posZ1 < 0)
                                    --posZ1;
                            }
                            if (Math.abs(entity.posZ - (int) entity.posZ) > 0.7) {
                                posZ1 = (int) (entity.posZ + (1.3 * (1 - (entity.posZ - (int) entity.posZ))));
                                if(posZ1 < 0)
                                    --posZ1;
                            }
                            if(posX1 == 0)
                                posX1 = posX;
                            if(posZ1 == 0)
                                posZ1 = posZ;
                            blockOnMaterial = world.getBlock(posX1, posY, posZ1).getMaterial();
                            blockInMaterial = world.getBlock(posX1, posY + 1, posZ1).getMaterial();
                            blockOnBlock = world.getBlock(posX1, posY, posZ1);
                        }
                        if(blockOnMaterial == Material.grass || blockOnMaterial == Material.ground)
                            slownessTerrain = IguanaConfig.terrainSlowdownOnDirt;
                        else if(blockOnMaterial == Material.sand && blockOnBlock != Blocks.gravel)
                            slownessTerrain = IguanaConfig.terrainSlowdownOnSand;
                        else if(blockOnBlock == Blocks.gravel) //gravel
                            slownessTerrain = IguanaConfig.terrainSlowdownOnGravel;
                        else if(blockOnMaterial == Material.leaves || blockOnMaterial == Material.plants || blockOnMaterial == Material.vine)
                            slownessTerrain = IguanaConfig.terrainSlowdownOnPlant;
                        else if(blockOnMaterial == Material.ice)
                            slownessTerrain = IguanaConfig.terrainSlowdownOnIce;
                        else if(blockOnMaterial == Material.snow)
                            slownessTerrain = IguanaConfig.terrainSlowdownOnSnow;

                        if(blockInMaterial == Material.snow)
                            slownessTerrain += IguanaConfig.terrainSlowdownInSnow;
                        else if(blockInMaterial == Material.vine || blockOnMaterial == Material.plants)
                            slownessTerrain += IguanaConfig.terrainSlowdownInPlant;

                        if(blockOnMaterial == Material.ice)
                            onIce = true;

                        slownessTerrain = Math.round((float)slownessTerrain * ((float)IguanaConfig.terrainSlowdownPercentage / 100f));
                    }


                    if(entity instanceof EntityPlayer && IguanaConfig.maxCarryWeight > 0) {
                        EntityPlayer player = (EntityPlayer)entity;

                        for(Object slotObject : player.inventoryContainer.inventorySlots) {
                            Slot slot = (Slot)slotObject;
                            if(slot.getHasStack()) {
                                double toAdd;

                                ItemStack stack = slot.getStack();
                                Block block = Block.getBlockFromItem(stack.getItem());

                                if(block != null) {
                                    toAdd = IguanaWeightsConfig.getBlockWeight(block) * (double)IguanaConfig.rockWeight;
                                } else { //is item
                                    toAdd = 1d / 64d;
                                }
                                weight += toAdd * (double)stack.stackSize;
                            }
                        }

                        // Calculate max weight
                        maxWeight = (double)IguanaConfig.maxCarryWeight;

                        slownessWeight = (weight / maxWeight) * 100d;

                        if(slownessWeight > 0)
                            player.addExhaustion(0.0001F * Math.round(slownessWeight));
                    }

                    if(slownessArmour > 100d)
                        slownessArmour = 100d;
                    if(slownessHurt > 100)
                        slownessHurt = 100;
                    if(slownessTerrain > 100)
                        slownessTerrain = 100;
                    if(slownessWeight > 100d)
                        slownessWeight = 100d;
                    double speedModifierArmour = (100d - slownessArmour) / 100d;
                    double speedModifierHurt = (100d - (double)slownessHurt) / 100d;
                    double speedModifierTerrain = (100d - (double)slownessTerrain) / 100d;
                    double speedModifierWeight = (100d - slownessWeight) / 100d;

                    //if (entity instanceof EntityPlayer) FMLLog.warning("speedModifier" + Double.toString(speedModifier));
                    //if (world.isRemote && entity instanceof EntityPlayer) FMLLog.warning("ssp" + Double.toString(speedModifier));
                    //if (!world.isRemote && entity instanceof EntityPlayer) FMLLog.warning("smp" + Double.toString(speedModifier));

                    speedModifier = speedModifierArmour * speedModifierHurt * speedModifierTerrain * speedModifierWeight;

                    if(entity instanceof EntityPlayerSP) {
                        EntityPlayerSP playerSP = (EntityPlayerSP)entity;
                        if(playerSP.moveForward < 0f)
                            speedModifier *= 0.5d;
                    }
                    if(onIce)
                        speedModifier = 0.8d + (speedModifier / 5d);

                    //if (entity instanceof EntityPlayer) FMLLog.warning("onIce" + Boolean.toString(onIce) + " mod" + Double.toString(speedModifier));

                }

				/*
				if (entity instanceof EntityPlayer)
					IguanaLog.log("hadValue" + Boolean.toString(IguanaTweaks.entityDataMap.containsKey(entity.entityUniqueID)) + " time" + Boolean.toString(entity.entityAge % 10 == 0));
				 */

                EntityData entityData = new EntityData(speedModifier, weight, maxWeight, slownessWeight, slownessArmour);
                IguanaTweaks.entityDataMap.put(entity.getUniqueID(), entityData);
            }
            if(speedModifier != 1d && !IguanaUtils.isJumping(entity)) {
                speedModifier = (2d * speedModifier) - 1d;
                entity.motionX *= speedModifier;
                entity.motionZ *= speedModifier;
            }
        }
    }

	/*
	@ForgeSubscribe
	public void LivingHurt(LivingHurtEvent event)
	{
		if (IguanaConfig.damageSlowdownDuration > 0 && !event.entityLiving.worldObj.isRemote)
		{
			float difficultyEffect = 1;
			if (IguanaConfig.damageSlowdownDifficultyScaling)
			{
				int difficulty = event.entityLiving.worldObj.difficultySetting;
				if (!(event.entityLiving instanceof EntityPlayer)) difficulty = (difficulty - 3) * -1;
				difficultyEffect = Math.max(difficulty - 1, 0);
			}

			int duration = Math.max(Math.min(Math.round(event.ammount * 5f * difficultyEffect), 255), 0);
			int effect = duration;


			//if (event.entityLiving instanceof EntityPlayer) 
			//	FMLLog.warning("a" + Float.toString(event.ammount) + " d" + duration + " e" + effect + " dif" + Float.toString(difficultyEffect));

			if (duration > 0)
			{
	        	PotionEffect currentEffect = event.entityLiving.getActivePotionEffect(IguanaTweaks.slowdownNew);
	        	if (currentEffect != null) {
	        		duration = Math.min(duration + currentEffect.duration, 255);
	        		effect = Math.min(effect + currentEffect.getAmplifier(), 255);

	    			//if (event.entityLiving instanceof EntityPlayer) 
	    			//	FMLLog.warning("cd" + currentEffect.duration + " da" + duration + " ce" + currentEffect.getAmplifier() + " ea" + effect);

	        	}
	        	event.entityLiving.addPotionEffect(new PotionEffect(IguanaTweaks.slowdownNew.id, duration, effect, true));
			}
		}
	}
	 */

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void LivingDrops(LivingDropsEvent event) {
        if(event.entityLiving != null) {
            boolean isPlayer = event.entityLiving instanceof EntityPlayer;

            if(IguanaConfig.restrictedDrops.size() > 0 || (IguanaConfig.itemLifespanPlayerDeath != 6000 && isPlayer) || (IguanaConfig.itemLifespanMobDeath != 6000 && !isPlayer)) {
                Iterator<EntityItem> i = event.drops.iterator();
                while(i.hasNext()) {
                    EntityItem eitem = i.next();

                    if(eitem != null && eitem.getEntityItem() != null) {
                        ItemStack item = eitem.getEntityItem();

                        if(!isPlayer && (IguanaConfig.restrictedDrops.contains(Item.itemRegistry.getNameForObject(item)) || IguanaConfig.restrictedDrops.contains(Item.itemRegistry.getNameForObject(item) + ":" + Integer.toString(item.getItemDamage())))) {
                            i.remove();
                        } else if(IguanaConfig.itemLifespanPlayerDeath != 6000 && isPlayer) {
                            eitem.lifespan = IguanaConfig.itemLifespanPlayerDeath;
                        } else if(IguanaConfig.itemLifespanMobDeath != 6000 && !isPlayer) {
                            eitem.lifespan = IguanaConfig.itemLifespanMobDeath;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onItemTossEvent(ItemTossEvent event) {
        if(event.entityItem != null)
            event.entityItem.lifespan = IguanaConfig.itemLifespanTossed;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if(IguanaConfig.maxCarryWeight > 0 || IguanaConfig.armorWeight > 0d) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.thePlayer;

            if(IguanaConfig.showCreativeText && !mc.gameSettings.showDebugInfo && player.capabilities.isCreativeMode) {
                event.left.add("Creative Mode");
            }

            if(IguanaTweaks.entityDataMap.containsKey(player.getUniqueID())) {
                EntityData playerWeightValues = IguanaTweaks.entityDataMap.get(player.getUniqueID());

                if(mc.gameSettings.showDebugInfo && IguanaConfig.addEncumbranceDebugText) {
                    event.left.add("");
                    event.left.add("Weight: " + String.format("%.2f", playerWeightValues.currentWeight) + " / " + String.format("%.2f", playerWeightValues.maxWeight) + " (" + String.format("%.2f", playerWeightValues.encumberance) + "%)");
                } else if(!player.isDead && !player.capabilities.isCreativeMode && IguanaConfig.addEncumbranceHudText) {
                    String color = "\u00A7f";

                    String line = "";

                    if(IguanaConfig.detailedEncumbranceHudText) {
                        if(playerWeightValues.encumberance >= 30)
                            color = "\u00A74";
                        else if(playerWeightValues.encumberance >= 20)
                            color = "\u00A76";
                        else if(playerWeightValues.encumberance >= 10)
                            color = "\u00A7e";

                        line = "Weight: " + Double.toString(Math.round(playerWeightValues.currentWeight)) + " / " + Double.toString(Math.round(playerWeightValues.maxWeight)) + " (" + String.format("%.2f", playerWeightValues.encumberance) + "%)";
                    } else {
                        double totalEncumberance = playerWeightValues.encumberance + playerWeightValues.armour;

                        if(totalEncumberance >= 30)
                            color = "\u00A74";
                        else if(totalEncumberance >= 20)
                            color = "\u00A76";
                        else if(totalEncumberance >= 10)
                            color = "\u00A7e";

                        if(totalEncumberance >= 30) {
                            line = "Greatly encumbered";
                        } else if(totalEncumberance >= 20) {
                            line = "Encumbered";
                        } else if(totalEncumberance >= 10) {
                            line = "Slightly encumbered";
                        }
                    }

                    if(!line.equals(""))
                        event.right.add(color + line + "\u00A7r");
                }
            }

        }
    }


    @SubscribeEvent
    public void playerSleep(PlayerSleepInBedEvent event) {
        if(IguanaConfig.disableSleeping) {
            event.result = EnumStatus.OTHER_PROBLEM;
            if(IguanaConfig.disableSettingSpawn) {
                event.entityPlayer.addChatMessage(new ChatComponentTranslation("msg.decorativeBeds"));
            } else {
                event.entityPlayer.setSpawnChunk(new ChunkCoordinates(event.x, event.y, event.z), false, event.entityPlayer.worldObj.provider.dimensionId);
                event.entityPlayer.addChatMessage(new ChatComponentTranslation("msg.setNoSleep"));
            }
        }
    }

    @SubscribeEvent
    public void onGenerateMinable(GenerateMinable event) {
        if((IguanaConfig.disableCoalGen && event.type == GenerateMinable.EventType.COAL) || (IguanaConfig.disableDiamondGen && event.type == GenerateMinable.EventType.DIAMOND) || (IguanaConfig.disableDirtGen && event.type == GenerateMinable.EventType.DIRT) || (IguanaConfig.disableGoldGen && event.type == GenerateMinable.EventType.GOLD) || (IguanaConfig.disableGravelGen && event.type == GenerateMinable.EventType.GRAVEL) || (IguanaConfig.disableIronGen && event.type == GenerateMinable.EventType.IRON) || (IguanaConfig.disableLapisGen && event.type == GenerateMinable.EventType.LAPIS) || (IguanaConfig.disableRedstoneGen && event.type == GenerateMinable.EventType.REDSTONE)) {
            event.setResult(Result.DENY);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if(event.entity != null) {
            if(event.entity instanceof EntityItem) {
                EntityItem item = (EntityItem)event.entity;
                if(item.lifespan != 6000 && IguanaConfig.itemLifespan != 6000)
                    item.lifespan = IguanaConfig.itemLifespan;
            } else if(event.entity instanceof EntityXPOrb) {
                EntityXPOrb orb = (EntityXPOrb)event.entity;

                if(IguanaConfig.itemLifespanXp != 6000) {
                    orb.xpOrbAge = 6000 - IguanaConfig.itemLifespanXp;
                }

                if(IguanaConfig.experiencePercentageAll != 100) {
                    orb.xpValue = Math.round((float)orb.xpValue * (IguanaConfig.experiencePercentageAll / 100f));
                    if(orb.xpValue == 0)
                        orb.xpOrbAge = 6000;
                }
            }
        }
    }

    @SubscribeEvent
    public void onBreakEvent(BreakEvent event) {
        if(IguanaConfig.experiencePercentageOre != 100) {
            event.setExpToDrop(Math.round((float)event.getExpToDrop() * (IguanaConfig.experiencePercentageOre / 100f)));
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(BreakSpeed event) {
        if(IguanaConfig.hardnessMultiplier != 1d && event.entityPlayer != null && event.block != null) {
            if((IguanaConfig.hardnessBlockListIsWhitelist && IguanaConfig.hardnessBlockList.contains(Block.blockRegistry.getNameForObject(event.block))) || (!IguanaConfig.hardnessBlockListIsWhitelist && !IguanaConfig.hardnessBlockList.contains(Block.blockRegistry.getNameForObject(event.block)))) {
                if(IguanaConfig.hardnessMultiplier == 0d)
                    event.newSpeed = Float.MAX_VALUE;
                else
                    event.newSpeed = event.originalSpeed / (float)IguanaConfig.hardnessMultiplier;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        if(event.dwheel != 0) {
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            NBTTagCompound tags = player.getEntityData();
            tags.removeTag("HideHotbarDelay");
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();

        if(event.type.equals(ElementType.ALL)) {
            int guiHeight = 39;
            if(!GuiIngameForge.renderExperiance)
                guiHeight -= 5;
            if(!GuiIngameForge.renderHotbar)
                guiHeight -= 22;
            GuiIngameForge.left_height = GuiIngameForge.right_height = guiHeight;
        }

        if(IguanaConfig.hideHungerBar && event.type.equals(ElementType.FOOD)) {
            EntityClientPlayerMP player = mc.thePlayer;
            NBTTagCompound tags = player.getEntityData();
            if(tags.getInteger("HideHungerBarDelay") >= IguanaConfig.hideHungerBarDelay * 20) {
                event.setCanceled(true);
            }
        } else if(IguanaConfig.hideHotbarBackground && event.type.equals(ElementType.HOTBAR) && !event.isCanceled()) {
            GuiIngame g = mc.ingameGUI;
            int width = event.resolution.getScaledWidth();
            int height = event.resolution.getScaledHeight();

            mc.mcProfiler.startSection("actionBar");

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(new ResourceLocation("textures/gui/widgets.png"));

            InventoryPlayer inv = mc.thePlayer.inventory;
            //g.drawTexturedModalRect(width / 2 - 91, height - 22, 0, 0, 182, 22);
            g.drawTexturedModalRect(width / 2 - 91 - 1 + inv.currentItem * 20, height - 22 - 1, 0, 22, 24, 22);

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();

            for(int i = 0; i < 9; ++i) {
                int x = width / 2 - 90 + i * 20 + 2;
                int z = height - 16 - 3;
                g.renderInventorySlot(i, x, z, event.partialTicks);
            }

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            mc.mcProfiler.endSection();

            event.setCanceled(true);
        }
    }

	/*
    @ForgeSubscribe
    public void playerDeath(LivingDeathEvent event)
    {

    	if (IguanaConfig.respawnLocationRandomisationMax > 0)
    	{
	    	// check it is a player that died
	    	if (!(event.entityLiving instanceof EntityPlayer)) return;

			IguanaLog.log("respawn code running playerDeath");

	    	// setup vars
	    	EntityPlayer player = (EntityPlayer)event.entityLiving;
	    	World world = player.worldObj;
	    	int dimension  = player.dimension;

	    	// save bed coordinates, if they exist
	    	ChunkCoordinates spawnLoc = player.getBedLocation(dimension);
	    	if (spawnLoc != null)
	    	{
	    		boolean forced = player.isSpawnForced(dimension);

		        NBTTagCompound tags = player.getEntityData();
		        if (!tags.hasKey("IguanaTweaks")) tags.setCompoundTag("IguanaTweaks", new NBTTagCompound());
		        NBTTagCompound tagsIguana = tags.getCompoundTag("IguanaTweaks");
		        tagsIguana.setBoolean("SpawnForced", forced);
		        tagsIguana.setInteger("SpawnDimension", dimension);
		        tagsIguana.setInteger("SpawnX", spawnLoc.posX);
		        tagsIguana.setInteger("SpawnY", spawnLoc.posY);
		        tagsIguana.setInteger("SpawnZ", spawnLoc.posZ);

		    	// get respawn coords
		    	ChunkCoordinates spawnCoords = IguanaPlayerHandler.randomiseCoordinates(world, spawnLoc.posX, spawnLoc.posZ, IguanaConfig.respawnLocationRandomisationMin, IguanaConfig.respawnLocationRandomisationMax);

		    	// save new "bed" coords
		    	if (spawnCoords != null) 
		    	{
		    		IguanaLog.log("Setting respawn coords on both sides for " + player.username + " to " + spawnCoords.posX + "," +  spawnCoords.posY + "," + spawnCoords.posZ);
		    		player.setSpawnChunk(spawnCoords, true, dimension);
		    	}
	    	}
	    	else if (player instanceof EntityPlayerMP)
    		{
    			EntityPlayerMP playerMP = (EntityPlayerMP)player;

		    	// get the players spawn coords
	    		if (!world.provider.canRespawnHere()) dimension = world.provider.getRespawnDimension(playerMP);
	            WorldServer worldserver = MinecraftServer.getServer().worldServerForDimension(dimension);
	    		spawnLoc = worldserver.getSpawnPoint();

		    	// get respawn coords
		    	ChunkCoordinates spawnCoords = IguanaPlayerHandler.randomiseCoordinates(world, spawnLoc.posX, spawnLoc.posZ, IguanaConfig.respawnLocationRandomisationMin, IguanaConfig.respawnLocationRandomisationMax);

		    	// save new "bed" coords
		    	if (spawnCoords != null) 
		    	{
		    		IguanaLog.log("Setting respawn coords server-side for " + player.username + " to " + spawnCoords.posX + "," +  spawnCoords.posY + "," + spawnCoords.posZ);
		    		playerMP.setSpawnChunk(spawnCoords, true, dimension);
		    		PacketDispatcher.sendPacketToPlayer(IguanaSpawnPacket.create(false, spawnCoords.posX, spawnCoords.posY, spawnCoords.posZ, true, dimension), (Player)player);
		    	}
    		}
    	}

    }*/

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if(event.showAdvancedItemTooltips) {
            event.toolTip.add("Weight: " + IguanaWeightsConfig.getBlockWeight(Block.getBlockFromItem(event.itemStack.getItem())));
        }
    }
}
