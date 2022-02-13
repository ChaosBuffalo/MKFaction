package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.event.MKFactionRegistry;
import com.chaosbuffalo.mkfaction.network.MKFactionUpdatePacket;
import com.chaosbuffalo.mkfaction.network.PacketHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;

import javax.annotation.Nonnull;
import java.util.Map;

public class FactionManager extends JsonReloadListener {
    public static final String DEFINITION_FOLDER = "factions";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public FactionManager() {
        super(GSON, DEFINITION_FOLDER);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn,
                         @Nonnull IResourceManager resourceManagerIn, @Nonnull IProfiler profilerIn) {
        MKFactionMod.LOGGER.info("In apply reload for FactionManager");
        for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
            ResourceLocation factionId = entry.getKey();
            if (factionId.getPath().startsWith("_"))
                continue; //Forge: filter anything beginning with "_" as it's used for metadata.

            MKFactionMod.LOGGER.info("Found file: {}", factionId);
            parseFaction(factionId, entry.getValue().getAsJsonObject());
        }
    }

    @SubscribeEvent
    public void subscribeEvent(AddReloadListenerEvent event) {
        event.addListener(this);
    }

    @SubscribeEvent
    public void onDataPackSync(OnDatapackSyncEvent event) {
        MKFactionMod.LOGGER.debug("FactionManager.onDataPackSync");
        MKFactionUpdatePacket updatePacket = new MKFactionUpdatePacket(MKFactionRegistry.FACTION_REGISTRY.getValues());
        if (event.getPlayer() != null) {
            // sync to single player
            MKFactionMod.LOGGER.debug("Sending {} faction definition update packet", event.getPlayer());
            event.getPlayer().connection.sendPacket(
                    PacketHandler.getNetworkChannel().toVanillaPacket(updatePacket, NetworkDirection.PLAY_TO_CLIENT));
        } else {
            // sync to playerlist
            event.getPlayerList().sendPacketToAllPlayers(
                    PacketHandler.getNetworkChannel().toVanillaPacket(updatePacket, NetworkDirection.PLAY_TO_CLIENT));
        }
    }

    private boolean parseFaction(ResourceLocation factionId, JsonObject json) {
        MKFactionMod.LOGGER.info("Parsing Faction Json for {}", factionId);
        MKFaction faction = MKFactionRegistry.getFaction(factionId);
        if (faction == null) {
            MKFactionMod.LOGGER.warn("Failed to parse faction data for : {}", factionId);
            return false;
        }
        faction.deserialize(new Dynamic<>(JsonOps.INSTANCE, json));
        MKFactionMod.LOGGER.info("Updated Faction: {} default score: {}", factionId, faction.getDefaultPlayerScore());
        return true;
    }
}
