package iguanaman.iguanatweaks;

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
