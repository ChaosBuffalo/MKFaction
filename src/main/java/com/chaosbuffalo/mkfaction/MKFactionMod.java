package com.chaosbuffalo.mkfaction;

import com.chaosbuffalo.mkfaction.capabilities.Capabilities;
import com.chaosbuffalo.mkfaction.faction.FactionDefaultManager;
import com.chaosbuffalo.mkfaction.faction.FactionManager;
import com.chaosbuffalo.mkfaction.network.PacketHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;


@Mod(MKFactionMod.MODID)
public class MKFactionMod
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "mkfaction";
    private FactionManager factionManager;
    private FactionDefaultManager factionDefaultManager;
    public static final KeyBinding CON_KEY_BIND = new KeyBinding("key.mkfaction.con.desc",
            GLFW.GLFW_KEY_C,
            "key.mkfaction.category");

    public MKFactionMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }


    private void setup(final FMLCommonSetupEvent event){
        PacketHandler.setupHandler();
        TargetingHooks.registerHooks();
        Capabilities.registerCapabilities();
    }

    private void clientSetup(final FMLClientSetupEvent event){
        LOGGER.info("Client setup");
        ClientRegistry.registerKeyBinding(CON_KEY_BIND);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void aboutToStart(FMLServerAboutToStartEvent event){
        factionManager = new FactionManager(event.getServer());
        factionDefaultManager = new FactionDefaultManager();
        event.getServer().getResourceManager().addReloadListener(factionManager);
        event.getServer().getResourceManager().addReloadListener(factionDefaultManager);
    }
}
