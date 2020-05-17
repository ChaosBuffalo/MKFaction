package com.chaosbuffalo.mkfaction;

import com.chaosbuffalo.mkfaction.capabilities.Capabilities;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(MKFactionMod.MODID)
public class MKFactionMod
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "mkfaction";

    public MKFactionMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

    }

    private void setup(final FMLCommonSetupEvent event){
        TargetingHooks.registerHooks();
        Capabilities.registerCapabilities();
    }
}
