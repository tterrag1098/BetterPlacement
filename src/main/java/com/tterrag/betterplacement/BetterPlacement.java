package com.tterrag.betterplacement;

import static com.tterrag.betterplacement.BetterPlacement.MOD_ID;
import static com.tterrag.betterplacement.BetterPlacement.MOD_NAME;
import static com.tterrag.betterplacement.BetterPlacement.VERSION;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.common.util.ForgeDirection;

@Mod(modid = MOD_ID, name = MOD_NAME, version = VERSION, guiFactory = "com.tterrag.betterplacement.GuiFactory", acceptableRemoteVersions = "*")
public class BetterPlacement {

    public static final String MOD_ID = "betterplacement";
    public static final String MOD_NAME = "Better Placement";
    public static final String VERSION = "@VERSION@";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configs.load(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(this);
    }

    private static BlockCoord lastTargetPos;
    private static ForgeDirection lastTargetSide;

    private static final Field _rightClickDelayTimer = ReflectionHelper.findField(Minecraft.class, "field_71467_ac", "rightClickDelayTimer");

    private static final MethodHandle getDelayTimer, setDelayTimer;
    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            getDelayTimer = lookup.unreflectGetter(_rightClickDelayTimer);
            setDelayTimer = lookup.unreflectSetter(_rightClickDelayTimer);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) throws Throwable {
        if (event.phase == Phase.START && (!Configs.creativeOnly || Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode)) {
            int timer = (int) getDelayTimer.invoke(Minecraft.getMinecraft());
            MovingObjectPosition hover = Minecraft.getMinecraft().objectMouseOver;
            if (hover != null && hover.typeOfHit == MovingObjectType.BLOCK) {
                BlockCoord pos = new BlockCoord(hover);
                if (timer > 0 && !pos.equals(lastTargetPos) && (lastTargetPos == null || !pos.equals(lastTargetPos.getLocation(lastTargetSide)))) {
                    setDelayTimer.invoke(Minecraft.getMinecraft(), 0);
                } else if (Configs.forceNewLoc && timer == 0 && pos.equals(lastTargetPos) && hover.sideHit == lastTargetSide.ordinal()) {
                    setDelayTimer.invoke(Minecraft.getMinecraft(), 4);
                }
                lastTargetPos = pos;
                lastTargetSide = ForgeDirection.getOrientation(hover.sideHit);
            }
        }
    }
}
