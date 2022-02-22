package com.chaosbuffalo.mkfaction.network;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.event.MKFactionRegistry;
import com.chaosbuffalo.mkfaction.faction.MKFaction;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class MKFactionDefinitionUpdatePacket {
    private final List<MKFactionData> factionData;

    private static class MKFactionData {
        public final MKFaction faction;
        public INBT encoded;

        public MKFactionData(MKFaction faction) {
            this.faction = faction;
        }
    }

    public MKFactionDefinitionUpdatePacket(Collection<MKFaction> factions) {
        this.factionData = new ArrayList<>();
        for (MKFaction faction : factions) {
            factionData.add(new MKFactionData(faction));
        }
    }

    public MKFactionDefinitionUpdatePacket(PacketBuffer buffer) {
        factionData = new ArrayList<>();
        int count = buffer.readInt();
        for (int i = 0; i < count; i++) {
            MKFaction faction = buffer.readRegistryIdUnsafe(MKFactionRegistry.FACTION_REGISTRY);
            if (faction != null) {
                MKFactionData data = new MKFactionData(faction);
                data.encoded = buffer.readCompoundTag();
                factionData.add(data);
            }
        }
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeInt(factionData.size());
        for (MKFactionData data : factionData) {
            buffer.writeRegistryIdUnsafe(MKFactionRegistry.FACTION_REGISTRY, data.faction);
            buffer.writeCompoundTag((CompoundNBT) data.faction.serialize(NBTDynamicOps.INSTANCE));
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        MKFactionMod.LOGGER.debug("Handling faction update packet");
        ctx.enqueueWork(() -> {
            for (MKFactionData data : factionData) {
                MKFaction faction = data.faction;
                MKFactionMod.LOGGER.debug("Parsing faction data: {}", faction.getRegistryName());

                faction.deserialize(new Dynamic<>(NBTDynamicOps.INSTANCE, data.encoded));
                MKFactionMod.LOGGER.info("Updated Faction: {} new score: {}",
                        faction.getRegistryName(), faction.getDefaultPlayerScore());
            }
        });
        ctx.setPacketHandled(true);
    }
}
