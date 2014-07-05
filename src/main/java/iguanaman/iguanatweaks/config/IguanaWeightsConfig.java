package iguanaman.iguanatweaks.config;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import iguanaman.iguanatweaks.IguanaTweaks;
import iguanaman.iguanatweaks.util.IguanaJsonReader;

public class IguanaWeightsConfig {

    private static HashMap<String, Double> weights;

    public static void init(File file) {
        if(file.exists()) {
            weights = IguanaJsonReader.readWeightsJson(file);
        } else {
            try {
                if(file.createNewFile()) {
                    PrintWriter writer = new PrintWriter(file);
                    writer.println("[");
                    writer.println("]");
                    writer.close();
                }
            } catch(IOException e) {
                IguanaTweaks.log.error("There was an error in creating the weights.json file");
                e.printStackTrace();
            }
        }
    }

    /**
     * Obtains the weight of a given block, either specified in the <I>weights.json</I> file, or by using it's material if the block is not specified in the <I>weights.json</I> file.
     * @param block The block to get the weight for
     * @return The weight of the block as a double
     */
    public static double getBlockWeight(Block block) {
        if(weights != null && weights.containsKey(Block.blockRegistry.getNameForObject(block))) {
            return weights.get(Block.blockRegistry.getNameForObject(block));
        } else {
            return getBlockWeightFromMaterial(block);
        }
    }

    private static double getBlockWeightFromMaterial(Block block) {
        Material blockMaterial = block.getMaterial();

        if(blockMaterial == Material.iron || blockMaterial == Material.anvil) {
            return 1.5D;
        } else if(blockMaterial == Material.rock) {
            return 1.0D;
        } else if(blockMaterial == Material.grass || blockMaterial == Material.ground || blockMaterial == Material.sand || blockMaterial == Material.snow || blockMaterial == Material.wood || blockMaterial == Material.glass || blockMaterial == Material.ice || blockMaterial == Material.tnt) {
            return 0.5D;
        } else if(blockMaterial == Material.cloth) {
            return 0.25D;
        } else if(block.isOpaqueCube()) {
            return 1D / 16D;
        } else {
            return 1D / 64D; // item like block
        }
    }
}
