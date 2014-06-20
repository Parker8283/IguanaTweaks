package iguanaman.iguanatweaks.util;

import iguanaman.iguanatweaks.IguanaTweaks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.google.gson.stream.JsonReader;

public class IguanaJsonReader {
	
	public static HashMap<String, Double> readWeightsJson(File file) {
		HashMap<String, Double> weights = new HashMap<String, Double>();
		String blockName = null;
		double weight = 0.0D;
		FileInputStream fis = null;
		JsonReader reader = null;
		
		try {
			fis = new FileInputStream(file);
			reader = new JsonReader(new InputStreamReader(fis, "UTF-8"));
			reader.beginArray();
			while(reader.hasNext()) {
				reader.beginObject();
				while(reader.hasNext()) {
					String name = reader.nextName();
					if(name.equalsIgnoreCase("block")) {
						blockName = reader.nextString();
					} else if(name.equalsIgnoreCase("weight")) {
						weight = reader.nextDouble();
					} else {
						reader.skipValue();
					}
				}
				reader.endObject();
				weights.put(blockName, weight);
			}
			reader.endArray();
		} catch(Exception e) {
			IguanaTweaks.log.error("There was an error in reading the weights.json file");
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					IguanaTweaks.log.error("There was an error in closing the weights.json reader");
					e.printStackTrace();
				}
			}
		}
		return weights;
	}
}
