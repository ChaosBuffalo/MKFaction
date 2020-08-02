package com.chaosbuffalo.mkfaction;

import com.chaosbuffalo.mkfaction.capabilities.Capabilities;
import com.chaosbuffalo.mkfaction.capabilities.PlayerFactionHandler;
import com.chaosbuffalo.mkfaction.command.FactionCommand;
import com.chaosbuffalo.mkfaction.event.InputHandler;
import com.chaosbuffalo.mkfaction.faction.FactionDefaultManager;
import com.chaosbuffalo.mkfaction.faction.FactionManager;
import com.chaosbuffalo.mkfaction.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(MKFactionMod.MODID)
public class MKFactionMod {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "mkfaction";
    private FactionManager factionManager;
    private FactionDefaultManager factionDefaultManager;

    public MKFactionMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        MinecraftForge.EVENT_BUS.register(this);
    }


    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("MKFactionMod.setup");
        PacketHandler.setupHandler();
        TargetingHooks.registerHooks();
        Capabilities.registerCapabilities();
        FactionCommand.registerArgumentTypes();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Client setup");
        InputHandler.registerKeybinds();
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void aboutToStart(FMLServerAboutToStartEvent event) {
        factionManager = new FactionManager(event.getServer());
        factionDefaultManager = new FactionDefaultManager();
        event.getServer().getResourceManager().addReloadListener(factionManager);
        event.getServer().getResourceManager().addReloadListener(factionDefaultManager);
    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
        FactionCommand.register(event.getCommandDispatcher());
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        PlayerFactionHandler.registerPersonaExtension();
    }
}
