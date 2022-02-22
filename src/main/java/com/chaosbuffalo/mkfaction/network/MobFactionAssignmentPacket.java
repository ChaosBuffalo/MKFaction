package com.chaosbuffalo.mkfaction.network;

import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.mkfaction.capabilities.IMobFaction;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MobFactionAssignmentPacket {

    private final ResourceLocation factionName;
    private final int entityId;

    public MobFactionAssignmentPacket(IMobFaction mobFaction) {
        entityId = mobFaction.getEntity().getEntityId();
        factionName = mobFaction.getFactionName();
    }

    public MobFactionAssignmentPacket(PacketBuffer buffer) {
        entityId = buffer.readInt();
        factionName = buffer.readResourceLocation();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeInt(entityId);
        buffer.writeResourceLocation(factionName);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> ClientHandler.handle(this));
        ctx.setPacketHandled(true);
    }

    public static class ClientHandler {
        public static void handle(MobFactionAssignmentPacket packet) {
            World world = Minecraft.getInstance().world;
            if (world == null) {
                return;
            }

            Entity entity = world.getEntityByID(packet.entityId);
            if (entity != null) {
                entity.getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY).ifPresent(mobFaction ->
                        mobFaction.setFactionName(packet.factionName));
            }
        }
    }
}