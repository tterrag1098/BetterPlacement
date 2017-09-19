package com.tterrag.betterplacement;

import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

public class ConfigGui extends GuiConfig {

    public ConfigGui(GuiScreen parentScreen) {
        super(parentScreen, new ConfigElement(Configs.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), BetterPlacement.MOD_ID, false, false,
                I18n.format(BetterPlacement.MOD_ID + ".config.title"));
    }
}
    