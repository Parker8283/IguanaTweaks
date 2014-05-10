package iguanaman.iguanatweaks;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.network.packet.Packet250CustomPayload;

public class IguanaSpawnPacket {

	public static Packet250CustomPayload create(boolean isNull, int x, int y, int z, boolean forced, int dimension) {

		Packet250CustomPayload packet = new Packet250CustomPayload();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
		        outputStream.writeBoolean(isNull);
		        outputStream.writeInt(x);
		        outputStream.writeInt(y);
		        outputStream.writeInt(z);
		        outputStream.writeBoolean(forced);
		        outputStream.writeInt(dimension);
		} catch (Exception ex) {
		        ex.printStackTrace();
		}

        packet.channel = "IguanaTweaks";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		
		return packet;
	}

}
