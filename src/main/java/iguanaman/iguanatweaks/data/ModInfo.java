package iguanaman.iguanatweaks.data;

public abstract class ModInfo {
	public static final String MODID = "IguanaTweaks";
	public static final String MOD_NAME = "Iguana Tweaks";
	public static final String VERSION = "@MOD_VERSION@";
	public static final String DEPENDENCIES = "required-after:Forge@[10.12.2.1121,);";
    public static final boolean IS_RELEASE = "@IS_RELEASE@".equals("true");
}
