package iguanaman.iguanatweaks;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.ForgeHooks;

public class XpTweaks {

	public static void init()
	{
		/*
		if (IguanaConfig.experiencePercentageSmelting != 100)
		{
			
	    	IguanaLog.log("Changing experience gained from smelting");
	    	
	    	// reflection to get map
	        Field f = null;
	        try {
	        	f = FurnaceRecipes.class.getDeclaredField("experienceList");
	        } catch (NoSuchFieldException e) {
	        	throw new RuntimeException("Could not access experienceList field, report please");
	        }
	        
	        f.setAccessible(true);
	        Map experienceList = new HashMap();
	        try {
	        	experienceList = (Map) f.get(FurnaceRecipes.smelting());   
	        } catch (IllegalAccessException e) {
	        	throw new RuntimeException("Could not access experienceList field, report please");
	        }

	        //the loop
	        for (Object smeltingEntryObject : experienceList.entrySet())
	        {
	        	Entry smeltingEntry = (Entry)smeltingEntryObject;
	        	if (smeltingEntry != null && smeltingEntry.getValue() != null)
	        	{
	        		float xp = (Float)smeltingEntry.getValue() * (IguanaConfig.experiencePercentageSmelting / 100f);
	        		smeltingEntry.setValue(Float.valueOf(xp));
	        	}
	        }
		}
		*/
	}

}
