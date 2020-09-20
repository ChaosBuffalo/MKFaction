package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.event.MKFactionRegistry;
import com.chaosbuffalo.mkfaction.network.MKFactionUpdatePacket;
import com.chaosbuffalo.mkfaction.network.PacketHandler;
import com.google.gson.*;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.Map;

public class FactionManager extends JsonReloadListener {
    private final MinecraftServer server;

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public FactionManager(MinecraftServer server) {
        super(GSON, "factions");
        this.server = server;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> objectIn, IResourceManager resourceManagerIn,
                         IProfiler profilerIn) {
        MKFactionMod.LOGGER.info("In apply reload for FactionManager");
        boolean wasChanged = false;
        for(Map.Entry<ResourceLocation, JsonObject> entry : objectIn.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            MKFactionMod.LOGGER.info("Found file: {}", resourcelocation);
            if (resourcelocation.getPath().startsWith("_")) continue; //Forge: filter anything beginning with "_" as it's used for metadata.
            if (parseFaction(entry.getKey(), entry.getValue())){
                wasChanged = true;
            }
        }
        if (wasChanged){
            syncToPlayers();
        }
    }

    public void syncToPlayers(){
        MKFactionUpdatePacket updatePacket = new MKFactionUpdatePacket(MKFactionRegistry.FACTION_REGISTRY.getValues());
        server.getPlayerList().sendPacketToAllPlayers(PacketHandler.getNetworkChannel().toVanillaPacket(
                updatePacket, NetworkDirection.PLAY_TO_CLIENT));
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
        int defaultPlayerScore = json.get("defaultPlayerScore").getAsInt();
        faction.setDefaultPlayerScore(defaultPlayerScore);
        JsonArray allies = json.get("allies").getAsJsonArray();
        faction.clearAllies();
        for (JsonElement ele : allies){
            String allyName = ele.getAsString();
            faction.addAlly(new ResourceLocation(allyName));
        }
        JsonArray enemies = json.get("enemies").getAsJsonArray();
        faction.clearEnemies();
        for (JsonElement ele : enemies){
            String enemyName = ele.getAsString();
            faction.addEnemy(new ResourceLocation(enemyName));
        }
        if (json.has("firstNames")){
            JsonArray firstNames = json.get("firstNames").getAsJsonArray();
            for (JsonElement firstName : firstNames){
                String firstNameStr = firstName.getAsString();
                faction.addFirstName(firstNameStr);
            }
        }
        if (json.has("lastNames")){
            JsonArray lastNames = json.get("lastNames").getAsJsonArray();
            for (JsonElement lastName : lastNames){
                String lastNameStr = lastName.getAsString();
                faction.addLastName(lastNameStr);
            }
        }
        MKFactionMod.LOGGER.info("Updated Faction: {} default score: {}", loc, faction.getDefaultPlayerScore());
        return true;
    }
}
