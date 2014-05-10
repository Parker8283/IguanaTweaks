package iguanaman.iguanatweaks;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet13PlayerLookMove;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet6SpawnPosition;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class IguanaPlayerHandler implements IPlayerTracker {

	@Override
   public void onPlayerLogin(EntityPlayer entityplayer) {
		
		if (IguanaConfig.spawnLocationRandomisationMax > 0)
		{
	        NBTTagCompound tags = entityplayer.getEntityData();
	        if (!tags.hasKey("IguanaTweaks")) tags.setCompoundTag("IguanaTweaks", new NBTTagCompound());
	        NBTTagCompound tagsIguana = tags.getCompoundTag("IguanaTweaks");
	        if (!tagsIguana.hasKey("Spawned"))
	        {
	            tagsIguana.setBoolean("Spawned", true);
				respawnPlayer((EntityPlayerMP)entityplayer, IguanaConfig.spawnLocationRandomisationMin, IguanaConfig.spawnLocationRandomisationMax);
	        }
		}
		
   }

	@Override
	public void onPlayerLogout(EntityPlayer player) {}
	
	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {}
	
	@Override
	public void onPlayerRespawn(EntityPlayer entityplayer) {
		
		// set health
	   int respawnHealth = IguanaConfig.respawnHealth;
	   
	   if (IguanaConfig.respawnHealthDifficultyScaling) {
		   if (entityplayer.worldObj.difficultySetting == 3) 
		   {
			   respawnHealth = Math.max(Math.round((float)respawnHealth / 2f), 1);
		   }
		   else if (entityplayer.worldObj.difficultySetting <= 1) 
		   {
			   respawnHealth = Math.min(respawnHealth * 2, 20);
		   }
	   }

		entityplayer.setHealth((float)respawnHealth);

		//split
		if (IguanaConfig.respawnLocationRandomisationMax > 0)
		{
			IguanaLog.log("respawn code running onPlayerRespawn");
			respawnPlayer((EntityPlayerMP)entityplayer, IguanaConfig.respawnLocationRandomisationMin, IguanaConfig.respawnLocationRandomisationMax);

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
    	    entityplayer.addChatMessage("You regain conciousness, confused as to where you are");
		}
		
		
		//split
		if (IguanaConfig.destroyBedOnRespawn)
		{
			ChunkCoordinates bedLoc = entityplayer.getBedLocation(entityplayer.dimension);
			if (bedLoc != null)
			{
			    // destroy nearest bed block (4 block radius)
				World world = entityplayer.worldObj;
                if (world.getBlockId(bedLoc.posX, bedLoc.posY, bedLoc.posZ) == Block.bed.blockID)
                {
                	BlockBed bed = (BlockBed)Block.bed;
                	int x = bedLoc.posX;
                	int z = bedLoc.posZ;
                	
                	//make sure destroying the correct bed part
                	int meta = world.getBlockMetadata(x, bedLoc.posY, z);
                	if (bed.isBlockHeadOfBed(meta))
                	{
            	        for (int testX = x - 1; testX <= x + 1; ++testX)
            	        {
        	                for (int testZ = z - 1; testZ <= z + 1; ++testZ)
        	                {
        	                    if (world.getBlockId(testX, bedLoc.posY, testZ) == Block.bed.blockID)
        	                    {
        	                    	meta = world.getBlockMetadata(testX, bedLoc.posY, testZ);
        	                    	if (!((BlockBed)Block.bed).isBlockHeadOfBed(meta)) 
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
    	    		entityplayer.worldObj.destroyBlock(x, bedLoc.posY, z, false);
    	    		entityplayer.setSpawnChunk(null, false, entityplayer.dimension);
    	    		if (IguanaConfig.respawnLocationRandomisationMax == 0)
    	    		{
    	    			entityplayer.addChatMessage("You awake to find your bed smashed to pieces");
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
				IguanaLog.log("good spawn found at " + newX + ", " + newY + ", " + newZ);
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
