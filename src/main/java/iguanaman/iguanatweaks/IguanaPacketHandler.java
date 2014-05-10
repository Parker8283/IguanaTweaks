package iguanaman.iguanatweaks;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ChunkCoordinates;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class IguanaPacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		
        if (packet.channel.equals("IguanaTweaks")) {
            DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
            
            boolean isNull;
            int x;
            int y;
            int z;
            boolean forced;
            int dimension;
           
            try {
            		isNull = inputStream.readBoolean();
	                x = inputStream.readInt();
	                y = inputStream.readInt();
	                z = inputStream.readInt();
                    forced = inputStream.readBoolean();
                    dimension = inputStream.readInt();
            } catch (IOException e) {
                    e.printStackTrace();
                    return;
            }
           
            EntityPlayer playerSP = ((EntityPlayer)player);
            IguanaLog.log("Setting respawn coords client-side for " + playerSP.username + " to " + x + "," +  y + "," + z);
            if (isNull)
                playerSP.setSpawnChunk(null, forced, dimension);
            else
            	playerSP.setSpawnChunk(new ChunkCoordinates(x, y, z), forced, dimension);
        }
        
	}


}
