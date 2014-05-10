package iguanaman.iguanatweaks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class StackSizeTweaks {

	public static void init() 
	{
    	// BLOCKS
    	if (IguanaConfig.blockStackSizeDividerMax > 1) 
    	{
    		IguanaLog.log("Reducing block stack sizes");
        	for (Block block : Block.blocksList)
        	{
        		
        		Item item = null;
        		try {
        			item = Item.itemsList[block.blockID];
        		} catch (Exception e) {
        			
        		}

        		if (item != null)
        		{
        			float blockWeight = (float)IguanaTweaks.getBlockWeight(block);
			        
			        int size = 0;
			        
			        if (blockWeight > 0d) 
			        {
		        		size = Math.round((float) item.maxStackSize / ((float) IguanaConfig.blockStackSizeDividerMax * blockWeight));
		        		if (size > item.maxStackSize / IguanaConfig.blockStackSizeDividerMin) size = item.maxStackSize / IguanaConfig.blockStackSizeDividerMin;
			        } else {
		        		size = Math.round((float)item.maxStackSize / (float)IguanaConfig.blockStackSizeDividerMin);
			        }

	        		if (size < 1) size = 1;
	        		if (size > 64) size = 64;
	        		if (size < item.maxStackSize) 
	        		{
	            		if (IguanaConfig.logStackSizeChanges) IguanaLog.log("Reducing stack size of block " + item.getUnlocalizedName()  + " to " + size);
	        			item.setMaxStackSize(size);
	        		}
        		}
        	}
    	}
    	
    	// ITEMS
    	if (IguanaConfig.itemStackSizeDivider > 1) 
    	{
    		IguanaLog.log("Reducing item stack sizes");
        	for (Item item : Item.itemsList)
        	{
        		if (item != null)
        		{
        			if (item.itemID >= Block.blocksList.length) 
        			{
        				int size = item.maxStackSize / IguanaConfig.itemStackSizeDivider;
		        		if (size < 1) size = 1;
		        		if (size > 64) size = 64;
		        		if (size < item.maxStackSize) 
		        		{
		        			if (IguanaConfig.logStackSizeChanges) IguanaLog.log("Reducing stack size of item " + item.getUnlocalizedName()  + " to " + size);
		        			item.setMaxStackSize(size);
		        		}
        			}
        		}
        	}
    	}
	}
	
}
