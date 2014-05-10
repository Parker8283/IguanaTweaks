package iguanaman.iguanatweaks;

import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class IguanaPlayerHandler {

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {

		if (IguanaConfig.spawnLocationRandomisationMax > 0)
		{
			NBTTagCompound tags = event.player.getEntityData();
			if (!tags.hasKey("IguanaTweaks")) tags.setTag("IguanaTweaks", new NBTTagCompound());
			NBTTagCompound tagsIguana = tags.getCompoundTag("IguanaTweaks");
			if (!tagsIguana.hasKey("Spawned"))
			{
				tagsIguana.setBoolean("Spawned", true);
				respawnPlayer((EntityPlayerMP)event.player, IguanaConfig.spawnLocationRandomisationMin, IguanaConfig.spawnLocationRandomisationMax);
			}
		}

	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {

		// set health
		int respawnHealth = IguanaConfig.respawnHealth;

		if (IguanaConfig.respawnHealthDifficultyScaling) {
			if (event.player.worldObj.difficultySetting.getDifficultyId() == 3) 
			{
				respawnHealth = Math.max(Math.round((float)respawnHealth / 2f), 1);
			}
			else if (event.player.worldObj.difficultySetting.getDifficultyId() <= 1) 
			{
				respawnHealth = Math.min(respawnHealth * 2, 20);
			}
		}

		event.player.setHealth((float)respawnHealth);

		//split
		if (IguanaConfig.respawnLocationRandomisationMax > 0)
		{
			IguanaTweaks.log.info("respawn code running onPlayerRespawn");
			respawnPlayer((EntityPlayerMP)event.player, IguanaConfig.respawnLocationRandomisationMin, IguanaConfig.respawnLocationRandomisationMax);

			/*
			boolean forced = false;
			int dimension = entityplayer.dimension;
			ChunkCoordinates spawnLoc = null;

			// reset bed coordinates
	        NBTTagCompound tags = entityplayer.getEntityData();
	        if (tags.hasKey("IguanaTweaks")) 
	        {
	        	NBTTagCompound tagsIguana = tags.getCompoundTag("IguanaTweaks");
	        	if (tagsIguana.hasKey("SpawnForced"))
	        	{
	        		// get stored bed coords
	        		forced = tagsIguana.getBoolean("SpawnForced");
	        		dimension = tagsIguana.getInteger("SpawnDimension");
	        		int bedX = tagsIguana.getInteger("SpawnX");
	        		int bedY = tagsIguana.getInteger("SpawnY");
	        		int bedZ = tagsIguana.getInteger("SpawnZ");

	        		// update bed location
		        	spawnLoc = new ChunkCoordinates(bedX, bedY, bedZ);

		        	// delete stored bed coords
		        	tagsIguana.removeTag("SpawnForced");
		        	tagsIguana.removeTag("SpawnDimension");
		        	tagsIguana.removeTag("SpawnX");
		        	tagsIguana.removeTag("SpawnY");
		        	tagsIguana.removeTag("SpawnZ");
	        	}
	        }

    		// unset fake bed coords
    		entityplayer.setSpawnChunk(spawnLoc, forced, dimension);
    		if (spawnLoc == null)
    			PacketDispatcher.sendPacketToPlayer(IguanaSpawnPacket.create(true, 0, 0, 0, forced, dimension), (Player)entityplayer);
    		else
    			PacketDispatcher.sendPacketToPlayer(IguanaSpawnPacket.create(false, spawnLoc.posX, spawnLoc.posY, spawnLoc.posZ, forced, dimension), (Player)entityplayer);
			 */

			// send a msg to the player
			event.player.addChatMessage(new ChatComponentTranslation("msg.randReSpawn"));
		}


		//split
		if (IguanaConfig.destroyBedOnRespawn)
		{
			ChunkCoordinates bedLoc = event.player.getBedLocation(event.player.dimension);
			if (bedLoc != null)
			{
				// destroy nearest bed block (4 block radius)
				World world = event.player.worldObj;
				if (world.getBlock(bedLoc.posX, bedLoc.posY, bedLoc.posZ) == Blocks.bed)
				{
					int x = bedLoc.posX;
					int z = bedLoc.posZ;

					//make sure destroying the correct bed part
					int meta = world.getBlockMetadata(x, bedLoc.posY, z);
					if (BlockBed.isBlockHeadOfBed(meta))
					{
						for (int testX = x - 1; testX <= x + 1; ++testX)
						{
							for (int testZ = z - 1; testZ <= z + 1; ++testZ)
							{
								if (world.getBlock(testX, bedLoc.posY, testZ) == Blocks.bed)
								{
									meta = world.getBlockMetadata(testX, bedLoc.posY, testZ);
									if (!BlockBed.isBlockHeadOfBed(meta)) 
									{
										x = testX;
										z = testZ;
										break;
									}
								}
							}
						}
					}

					//destroy bed
					event.player.worldObj.func_147480_a(x, bedLoc.posY, z, false);
					event.player.setSpawnChunk(null, false, event.player.dimension);
					if (IguanaConfig.respawnLocationRandomisationMax == 0)
					{
						event.player.addChatMessage(new ChatComponentTranslation("msg.bedGone"));
					}
				}
			}
		}

	}

	public static ChunkCoordinates randomiseCoordinates(World world, int x, int z, int rndFactorMin, int rndFactorMax)
	{

		int newX = -1;
		int newZ = -1;
		int newY = -1;

		// try 10 times to get a new spawn location
		for (int attempt = 1; attempt <= 10; ++attempt)
		{
			//IguanaLog.log("spawn finder attempt " + attempt);

			// get new x
			int modX = rndFactorMin;
			if ((rndFactorMax - rndFactorMin) > 0) modX += world.rand.nextInt(rndFactorMax - rndFactorMin);  
			if (world.rand.nextInt(100) < 50) modX *= -1;
			newX = x + modX;

			// get new z
			int modZ = rndFactorMin;
			if ((rndFactorMax - rndFactorMin) > 0) modZ += world.rand.nextInt(rndFactorMax - rndFactorMin);  
			if (world.rand.nextInt(100) < 50) modZ *= -1;
			newZ = z + modZ;

			// check for ocean biome
			BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(newX, newZ);
			Type[] biomeTypes = BiomeDictionary.getTypesForBiome(biome);
			if (biomeTypes.length == 1 && biomeTypes[0] == Type.WATER) continue;

			//get new y
			newY = world.getTopSolidOrLiquidBlock(newX, newZ);

			// found the topmost block?
			if (newY >= 0) 
			{	
				// good spawn location found
				IguanaTweaks.log.info("Good spawn found at " + newX + ", " + newY + ", " + newZ);
				return new ChunkCoordinates(newX, newY, newZ);
			}
		}

		// if failed
		return null;
	}

	public void respawnPlayer(EntityPlayerMP player, int rndFactorMin, int rndFactorMax)
	{
		int x = (int)player.posX;
		int z = (int)player.posZ;
		if (x < 0) --x;
		if (z < 0) --z;

		// get world object
		World world = player.worldObj;

		ChunkCoordinates newCoords = randomiseCoordinates(world, x, z, rndFactorMin, rndFactorMax);

		if (newCoords != null)
		{

			// move the player
			player.setLocationAndAngles((double)((float)newCoords.posX + 0.5F), (double)((float)newCoords.posY + 1.1F), (double)((float)newCoords.posZ + 0.5F), 0.0F, 0.0F);


			//WorldServer worldserver = MinecraftServer.getServer().worldServerForDimension(player.dimension);
			WorldServer worldserver = player.getServerForPlayer();
			worldserver.theChunkProviderServer.loadChunk((int)player.posX >> 4, (int)player.posZ >> 4);

			while (!worldserver.getCollidingBoundingBoxes(player, player.boundingBox).isEmpty())
			{
				player.setPosition(player.posX, player.posY + 1.0D, player.posZ);
			}

			player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		}

	}
}
