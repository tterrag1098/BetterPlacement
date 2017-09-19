package com.tterrag.betterplacement;

import static com.tterrag.betterplacement.BetterPlacement.MOD_ID;
import static com.tterrag.betterplacement.BetterPlacement.MOD_NAME;
import static com.tterrag.betterplacement.BetterPlacement.VERSION;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = MOD_ID, name = MOD_NAME, version = VERSION, clientSideOnly = true, acceptedMinecraftVersions = "[1.9.4, 1.13)", guiFactory = "com.tterrag.betterplacement.GuiFactory")
@EventBusSubscriber(Side.CLIENT)
public class BetterPlacement {

    public static final String MOD_ID = "betterplacement";
    public static final String MOD_NAME = "Better Placement";
    public static final String VERSION = "@VERSION@";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configs.load(event.getSuggestedConfigurationFile());
    }

    private static BlockPos lastTargetPos;
    private static EnumFacing lastTargetSide;

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
    public static void onClientTick(ClientTickEvent event) throws Throwable {
        if (event.phase == Phase.START && (!Configs.creativeOnly || Minecraft.getMinecraft().player.isCreative())) {
            int timer = (int) getDelayTimer.invoke(Minecraft.getMinecraft());
            RayTraceResult hover = Minecraft.getMinecraft().objectMouseOver;
            if (hover != null && hover.typeOfHit == Type.BLOCK) {
                BlockPos pos = hover.getBlockPos();
                if (timer > 0 && !pos.equals(lastTargetPos) && (lastTargetPos == null || !pos.equals(lastTargetPos.offset(lastTargetSide)))) {
                    System.out.println(lastTargetPos + " " + lastTargetSide);
                    setDelayTimer.invoke(Minecraft.getMinecraft(), 0);
                } else if (Configs.forceNewLoc && timer == 0 && pos.equals(lastTargetPos) && hover.sideHit == lastTargetSide) {
                    setDelayTimer.invoke(Minecraft.getMinecraft(), 4);
                }
                lastTargetPos = pos.toImmutable();
                lastTargetSide = hover.sideHit;
            }
        }
    }
}
