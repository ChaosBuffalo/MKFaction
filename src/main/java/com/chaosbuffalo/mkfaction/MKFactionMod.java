package com.chaosbuffalo.mkfaction;

import com.chaosbuffalo.mkfaction.capabilities.Capabilities;
import com.chaosbuffalo.mkfaction.faction.FactionManager;
import com.chaosbuffalo.mkfaction.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



@Mod(MKFactionMod.MODID)
public class MKFactionMod
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "mkfaction";
    private FactionManager factionManager;

    public MKFactionMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }


    private void setup(final FMLCommonSetupEvent event){
        LOGGER.info("Common setup");
        PacketHandler.setupHandler();
        TargetingHooks.registerHooks();
        Capabilities.registerCapabilities();
    }

    private void clientSetup(final FMLClientSetupEvent event){
        LOGGER.info("Client setup");
    }

    @SubscribeEvent
    public void aboutToStart(FMLServerAboutToStartEvent event){
        LOGGER.info("Server bout to start");
        factionManager = new FactionManager(event.getServer());
        event.getServer().getResourceManager().addReloadListener(factionManager);
    }
}
