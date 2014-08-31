package iguanaman.iguanatweaks.config;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class IguanaConfigGui extends GuiConfig {

    public IguanaConfigGui(GuiScreen parent) {
        super(parent, getConfigElements(), "IguanaTweaks", false, false, "Iguana Tweaks");
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> elements = Lists.newArrayList();

        for(String category : IguanaConfig.CATEGORIES) {
            elements.add(new ConfigElement(IguanaConfig.config.getCategory(category)));
        }
        return elements;
    }
}
