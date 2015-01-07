package iguanaman.iguanatweaks.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.google.gson.stream.JsonReader;

import iguanaman.iguanatweaks.IguanaTweaks;

public class IguanaJsonReader {

    public static HashMap<String, Double> readWeightsJson(File file) {
        HashMap<String, Double> weights = new HashMap<String, Double>();
        String objName = null;
        double weight = 0.0D;
        FileInputStream fis;
        JsonReader reader = null;

        try {
            fis = new FileInputStream(file);
            reader = new JsonReader(new InputStreamReader(fis, "UTF-8"));
            reader.beginArray();
            while(reader.hasNext()) {
                reader.beginObject();
                while(reader.hasNext()) {
                    String name = reader.nextName();
                    if(name.equalsIgnoreCase("name")) {
                        objName = reader.nextString();
                    } else if(name.equalsIgnoreCase("block")) {
                        IguanaTweaks.log.warn("The \"block\" key in the weights.json file has been deprecated. Change it to \"name\"");
                        objName = reader.nextString();
                    } else if(name.equalsIgnoreCase("weight")) {
                        weight = reader.nextDouble();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                weights.put(objName, weight);
            }
            reader.endArray();
        } catch(Exception e) {
            IguanaTweaks.log.error("There was an error in reading the weights.json file");
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch(IOException e) {
                    IguanaTweaks.log.error("There was an error in closing the weights.json reader");
                    e.printStackTrace();
                }
            }
        }
        return weights;
    }
}
