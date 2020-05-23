package com.chaosbuffalo.mkfaction.network;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.event.MKFactionRegistry;
import com.chaosbuffalo.mkfaction.faction.MKFaction;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class MKFactionUpdatePacket {
    private final List<MKFactionData> factionData;

    private static class MKFactionData {
        public ResourceLocation name;
        public List<ResourceLocation> allies;
        public List<ResourceLocation> enemies;
        public int defaultFactionScore;

        public MKFactionData(ResourceLocation name, int defaultScoreIn){
            allies = new ArrayList<>();
            enemies = new ArrayList<>();
            defaultFactionScore = defaultScoreIn;
            this.name = name;
        }

        public MKFactionData(MKFaction faction){
            name = faction.getRegistryName();
            allies = new ArrayList<>(faction.getAllies());
            enemies = new ArrayList<>(faction.getEnemies());
            defaultFactionScore = faction.getDefaultPlayerScore();
        }
    }

    public MKFactionUpdatePacket(Collection<MKFaction> factions){
        this.factionData = new ArrayList<>();
        for (MKFaction faction : factions){
            factionData.add(new MKFactionData(faction));
        }
    }

    public MKFactionUpdatePacket(PacketBuffer buffer){
        factionData = new ArrayList<>();
        int count = buffer.readInt();
        for (int i=0; i < count; i++){
            ResourceLocation factionName = buffer.readResourceLocation();
            int factionScore = buffer.readInt();
            MKFactionData data = new MKFactionData(factionName, factionScore);
            int allyCount = buffer.readInt();
            for (int aI=0; aI < allyCount; aI++){
                data.allies.add(buffer.readResourceLocation());
            }
            int enemyCount = buffer.readInt();
            for (int eI=0; eI < enemyCount; eI++){
                data.enemies.add(buffer.readResourceLocation());
            }
            factionData.add(data);
        }
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeInt(factionData.size());
        for (MKFactionData faction : factionData){
            buffer.writeResourceLocation(faction.name);
            buffer.writeInt(faction.defaultFactionScore);
            buffer.writeInt(faction.allies.size());
            for (int i=0; i < faction.allies.size(); i++){
                buffer.writeResourceLocation(faction.allies.get(i));
            }
            buffer.writeInt(faction.enemies.size());
            for (int i=0; i < faction.enemies.size(); i++){
                buffer.writeResourceLocation(faction.enemies.get(i));
            }
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        MKFactionMod.LOGGER.info("Handling faction update packet");
        ctx.enqueueWork(() -> {
           for (MKFactionData data : factionData){
               MKFactionMod.LOGGER.info("Parsing data: {}", data.name);
               MKFaction faction = MKFactionRegistry.getFaction(data.name);
               if (faction != null){
                   MKFactionMod.LOGGER.info("Reloading Faction: {}", data.name);
                   faction.setDefaultPlayerScore(data.defaultFactionScore);
                   faction.clearAllies();
                   for (ResourceLocation ally : data.allies){
                       faction.addAlly(ally);
                   }
                   faction.clearEnemies();
                   for (ResourceLocation enemy : data.enemies){
                       faction.addEnemy(enemy);
                   }
                   MKFactionMod.LOGGER.info("Updated Faction: {} new score: {}",
                           faction.getRegistryName(), faction.getDefaultPlayerScore());
               }
           }
        });
        ctx.setPacketHandled(true);
    }
}
