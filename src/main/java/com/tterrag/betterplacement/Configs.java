package com.tterrag.betterplacement;

import java.io.File;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

public class Configs {

    public static boolean creativeOnly;

    public static boolean forceNewLoc = false;
    
    static Configuration config;
    
    public static void load(File configFile) {
        config = new Configuration(configFile);
        sync();
        
        FMLCommonHandler.instance().bus().register(new Configs());
    }
    
    public static void sync() {
        creativeOnly    = config.getBoolean("creativeOnly", Configuration.CATEGORY_GENERAL, creativeOnly, "If true, the modifications will only apply in creative mode.");
        forceNewLoc     = config.getBoolean("forceNewLoc", Configuration.CATEGORY_GENERAL, forceNewLoc, "When true, a held right click will never place two blocks in a row, the player must move the cursor to a new location.");
        
        config.save();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(BetterPlacement.MOD_ID)) {
            sync();
        }
    }
}
