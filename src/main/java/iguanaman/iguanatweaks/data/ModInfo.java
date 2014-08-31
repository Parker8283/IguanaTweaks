package iguanaman.iguanatweaks.data;

public abstract class ModInfo {
    public static final String MODID = "IguanaTweaks";
    public static final String MOD_NAME = "Iguana Tweaks";
    public static final String VERSION = "@MOD_VERSION@";
    public static final String DEPENDENCIES = "required-after:Forge@[10.13.0.1185,);";
    public static final String MOD_GUI_FACTORY = "iguanaman.iguanatweaks.config.IguanaGuiFactory";
    public static final boolean IS_RELEASE = Boolean.parseBoolean("@IS_RELEASE@");
}
