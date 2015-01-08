package iguanaman.iguanatweaks.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import iguanaman.iguanatweaks.IguanaTweaks;
import iguanaman.iguanatweaks.config.IguanaConfig;
import iguanaman.iguanatweaks.config.IguanaWeightsConfig;

public class StackSizeTweaks {

    public static void init() {
        IguanaTweaks.log.info("Reducing stack sizes");
        boolean doBlocks = IguanaConfig.blockStackSizeDividerMax > 1;
        boolean doItems = IguanaConfig.itemStackSizeDivider > 1;
        for(Object obj : Item.itemRegistry) {
            if(obj instanceof ItemBlock && doBlocks) {
                Block block = ((ItemBlock)obj).field_150939_a;
                ItemStack stack = new ItemStack(block);
                float weight = (float)IguanaWeightsConfig.getWeight(stack);
                int size = 0;
                if(weight > 0) {
                    size = Math.round((float)stack.getItem().getItemStackLimit(stack) / ((float)IguanaConfig.blockStackSizeDividerMax * weight));
                    if(size > stack.getItem().getItemStackLimit(stack) / IguanaConfig.blockStackSizeDividerMin) {
                        size = stack.getItem().getItemStackLimit(stack) / IguanaConfig.blockStackSizeDividerMin;
                    }
                } else {
                    size = Math.round((float)stack.getItem().getItemStackLimit(stack) / (float)IguanaConfig.blockStackSizeDividerMin);
                }
                if(size < 1) {
                    size = 1;
                } else if(size > 64) {
                    size = 64;
                }
                if(size < stack.getItem().getItemStackLimit(stack)) {
                    stack.getItem().setMaxStackSize(size);
                }
                if(IguanaConfig.logStackSizeChanges) {
                    IguanaTweaks.log.info("Reduced stack size of block " + GameRegistry.findUniqueIdentifierFor(block).toString() + " to " + size);
                }
            } else if(doItems) {
                Item item = (Item)obj;
                ItemStack stack = new ItemStack(item);
                float weight = (float)IguanaWeightsConfig.getWeight(stack);
                int size = 0;
                if(weight > 0) {
                    size = Math.round((float)item.getItemStackLimit(stack) / ((float)IguanaConfig.itemStackSizeDivider * weight));
                } else {
                    size = Math.round((float)item.getItemStackLimit(stack) / (float)IguanaConfig.itemStackSizeDivider);
                }
                if(size < 1) {
                    size = 1;
                } else if(size > 64) {
                    size = 64;
                }
                if(size < item.getItemStackLimit(stack)) {
                    item.setMaxStackSize(size);
                }
                if(IguanaConfig.logStackSizeChanges) {
                    IguanaTweaks.log.info("Reduced stack size of item " + GameRegistry.findUniqueIdentifierFor(item).toString() + " to " + size);
                }
            }
        }
    }
}
