package iguanaman.iguanatweaks.config;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

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
     * Obtains the weight of a given block or item, either specified in the <I>weights.json</I> file, or by using it's default value if a different one is not specified in the <I>weights.json</I> file.<br/>
     * Blocks' default value are based on their material, while all Items are one constant value.
     * @param stack The ItemStack to get the weight for
     * @return The weight of the block as a double
     */
    public static double getWeight(ItemStack stack) {
        if(weights != null && weights.containsKey(Item.itemRegistry.getNameForObject(stack.getItem()))) {
            return weights.get(Item.itemRegistry.getNameForObject(stack.getItem()));
        } else {
            return getDefaultWeight(stack);
        }
    }

    private static double getDefaultWeight(ItemStack stack) {
        if(!(stack.getItem() instanceof ItemBlock)) {
            return 1D / 64D;
        }

        Block block = Block.getBlockFromItem(stack.getItem());
        Material blockMaterial = block.getMaterial();

        if(blockMaterial == Material.iron || blockMaterial == Material.anvil) {
            return 1.5D;
        } else if(blockMaterial == Material.rock) {
            return 1.0D;
        } else if(blockMaterial == Material.grass || blockMaterial == Material.ground || blockMaterial == Material.sand || blockMaterial == Material.snow || blockMaterial == Material.wood || blockMaterial == Material.glass || blockMaterial == Material.ice || blockMaterial == Material.tnt) {
            return 0.5D;
        } else if(blockMaterial == Material.cloth) {
            return 0.25D;
        } else {
            return 1D / 16D;
        }
    }
}
