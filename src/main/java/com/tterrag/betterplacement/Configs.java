package com.tterrag.betterplacement;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public class Configs {

    public static boolean creativeOnly;

    public static boolean forceNewLoc = true;
    
    static Configuration config;
    
    public static void load(File configFile) {
        config = new Configuration(configFile);
        sync();
    }
    
    public static void sync() {
        creativeOnly    = config.getBoolean("creativeOnly", Configuration.CATEGORY_CLIENT, creativeOnly, "If true, the modifications will only apply in creative mode.");
        forceNewLoc     = config.getBoolean("forceNewLoc", Configuration.CATEGORY_CLIENT, forceNewLoc, "When true, a held right click will never place two blocks in a row, the player must move the cursor to a new location.");
        
        config.save();
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(BetterPlacement.MOD_ID)) {
            sync();
        }
    }
}
