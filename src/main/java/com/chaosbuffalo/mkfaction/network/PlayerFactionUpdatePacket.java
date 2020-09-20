package com.chaosbuffalo.mkfaction.network;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.mkfaction.capabilities.IPlayerFaction;
import com.chaosbuffalo.mkfaction.faction.PlayerFactionEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.Map;

public class PlayerFactionUpdatePacket {

    private final UUID playerId;
    private final HashMap<ResourceLocation, Integer> factionScores;

    public PlayerFactionUpdatePacket(IPlayerFaction playerFaction) {
        playerId = playerFaction.getPlayer().getUniqueID();
        factionScores = new HashMap<>();
        for (Map.Entry<ResourceLocation, PlayerFactionEntry> entry : playerFaction.getFactionMap().entrySet()) {
            factionScores.put(entry.getKey(), entry.getValue().getFactionScore());
        }
    }

    public PlayerFactionUpdatePacket(PacketBuffer buffer){
        playerId = buffer.readUniqueId();
        int count = buffer.readInt();
        factionScores = new HashMap<>();
        for (int i = 0; i < count; i++){
            ResourceLocation factionName = buffer.readResourceLocation();
            int score = buffer.readInt();
            factionScores.put(factionName, score);
        }
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeUniqueId(playerId);
        buffer.writeInt(factionScores.size());
        for (Map.Entry<ResourceLocation, Integer> score : factionScores.entrySet()){
            buffer.writeResourceLocation(score.getKey());
            buffer.writeInt(score.getValue());
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            MKFactionMod.LOGGER.info("In receive packet for player faction update");
            World world = Minecraft.getInstance().world;
            if (world != null){
                Entity entity = world.getPlayerByUuid(playerId);
                if (entity != null){
                    entity.getCapability(FactionCapabilities.PLAYER_FACTION_CAPABILITY).ifPresent(playerFaction ->{
                        for (Map.Entry<ResourceLocation, Integer> score : factionScores.entrySet()){
                            playerFaction.getFactionEntry(score.getKey()).setFactionScore(score.getValue());
                        }
                    });
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
