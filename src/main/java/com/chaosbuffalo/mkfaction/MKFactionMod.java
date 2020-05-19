package com.chaosbuffalo.mkfaction;

import com.chaosbuffalo.mkfaction.capabilities.Capabilities;
import com.chaosbuffalo.mkfaction.faction.FactionManager;
import com.chaosbuffalo.mkfaction.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLCommonLaunchHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(MKFactionMod.MODID)
public class MKFactionMod
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "mkfaction";

    private final CommonProxy proxy;

    private abstract static class CommonProxy {

        public abstract void setupResourceManagers(IReloadableResourceManager resourceManager);

        public abstract FactionManager getFactionManager();
    }

    private static class ServerProxy extends CommonProxy {
        private FactionManager factionManager;

        @Override
        public void setupResourceManagers(IReloadableResourceManager resourceManager) {
            factionManager = new FactionManager();
            resourceManager.addReloadListener(factionManager);
        }

        @Override
        public FactionManager getFactionManager() {
            return factionManager;
        }
    }

    private static class ClientProxy extends CommonProxy {
        private FactionManager factionManager;

        @Override
        public void setupResourceManagers(IReloadableResourceManager resourceManager) {
            factionManager = new FactionManager();
            resourceManager.addReloadListener(factionManager);
        }

        @Override
        public FactionManager getFactionManager() {
            return factionManager;
        }
    }

    public MKFactionMod() {
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
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
        proxy.setupResourceManagers((IReloadableResourceManager) event.getMinecraftSupplier().get().getResourceManager());
    }

    @SubscribeEvent
    public void aboutToStart(FMLServerAboutToStartEvent event){
        LOGGER.info("Server bout to start");
        proxy.setupResourceManagers(event.getServer().getResourceManager());
    }
}
