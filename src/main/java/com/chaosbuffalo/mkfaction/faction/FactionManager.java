package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.event.MKFactionRegistry;
import com.chaosbuffalo.mkfaction.network.MKFactionUpdatePacket;
import com.chaosbuffalo.mkfaction.network.PacketHandler;
import com.google.gson.*;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.Map;

public class FactionManager extends JsonReloadListener {
    public static final String DEFINITION_FOLDER = "factions";
    private MinecraftServer server;
    private boolean serverStarted = false;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public FactionManager() {
        super(GSON, DEFINITION_FOLDER);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        MKFactionMod.LOGGER.info("In apply reload for FactionManager");
        boolean wasChanged = false;
        for(Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            MKFactionMod.LOGGER.info("Found file: {}", resourcelocation);
            if (resourcelocation.getPath().startsWith("_")) continue; //Forge: filter anything beginning with "_" as it's used for metadata.
            if (parseFaction(entry.getKey(), entry.getValue().getAsJsonObject())){
                wasChanged = true;
            }
        }
        if (serverStarted && wasChanged) {
            syncToPlayers();
        }
    }

    @SubscribeEvent
    public void serverStart(FMLServerAboutToStartEvent event) {
        serverStarted = true;
        server = event.getServer();
    }

    @SubscribeEvent
    public void serverStop(FMLServerStoppingEvent event) {
        serverStarted = false;
        server = null;
    }


    public void syncToPlayers(){
        MKFactionUpdatePacket updatePacket = new MKFactionUpdatePacket(MKFactionRegistry.FACTION_REGISTRY.getValues());
        server.getPlayerList().sendPacketToAllPlayers(PacketHandler.getNetworkChannel().toVanillaPacket(
                updatePacket, NetworkDirection.PLAY_TO_CLIENT));
    }

    @SubscribeEvent
    public void subscribeEvent(AddReloadListenerEvent event){
        event.addListener(this);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event){
        MKFactionMod.LOGGER.info("Player logged in faction manager");
        if (event.getPlayer() instanceof ServerPlayerEntity){
            MKFactionUpdatePacket updatePacket = new MKFactionUpdatePacket(
                    MKFactionRegistry.FACTION_REGISTRY.getValues());
            MKFactionMod.LOGGER.info("Sending {} update packet", event.getPlayer());
            ((ServerPlayerEntity) event.getPlayer()).connection.sendPacket(
                    PacketHandler.getNetworkChannel().toVanillaPacket(
                    updatePacket, NetworkDirection.PLAY_TO_CLIENT));
        }
    }

    private boolean parseFaction(ResourceLocation loc, JsonObject json){
        MKFactionMod.LOGGER.info("Parsing Faction Json for {}", loc);
        MKFaction faction = MKFactionRegistry.getFaction(loc);
        if (faction == null){
            MKFactionMod.LOGGER.warn("Failed to parse faction data for : {}", loc);
            return false;
        }
        faction.deserialize(new Dynamic<>(JsonOps.INSTANCE, json));
        MKFactionMod.LOGGER.info("Updated Faction: {} default score: {}", loc, faction.getDefaultPlayerScore());
        return true;
    }

}
