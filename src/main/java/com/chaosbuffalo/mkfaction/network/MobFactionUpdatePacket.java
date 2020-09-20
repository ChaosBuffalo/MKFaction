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

public class MobFactionUpdatePacket {

    private final ResourceLocation factionName;
    private final int entityId;

    public MobFactionUpdatePacket(IMobFaction mobFaction) {
        entityId = mobFaction.getEntity().getEntityId();
        factionName = mobFaction.getFactionName();
    }

    public MobFactionUpdatePacket(PacketBuffer buffer){
        entityId = buffer.readInt();
        factionName = buffer.readResourceLocation();
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeInt(entityId);
        buffer.writeResourceLocation(factionName);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            World world = Minecraft.getInstance().world;
            if (world != null){
                Entity entity = world.getEntityByID(entityId);
                if (entity != null){
                    entity.getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY).ifPresent(mobFaction ->{
                        mobFaction.setFactionName(factionName);
                    });
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
