package com.chaosbuffalo.mkfaction.capabilities;

import com.chaosbuffalo.mkfaction.event.MKFactionRegistry;
import com.chaosbuffalo.mkfaction.faction.MKFaction;
import com.chaosbuffalo.mkfaction.network.MobFactionUpdatePacket;
import com.chaosbuffalo.mkfaction.network.PacketHandler;
import com.chaosbuffalo.targeting_api.Targeting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

public class MobFactionHandler implements IMobFaction {
    private ResourceLocation factionName;
    private MKFaction faction;
    private LivingEntity entity;

    public MobFactionHandler() {
        factionName = MKFaction.INVALID_FACTION;
        entity = null;
    }

    @Nullable
    @Override
    public MKFaction getFaction() {
        return faction;
    }


    @Override
    public ResourceLocation getFactionName() {
        return factionName;
    }

    private void setFactionNameInternal(ResourceLocation factionName) {
        this.factionName = factionName;
        if (!factionName.equals(MKFaction.INVALID_FACTION)) {
            this.faction = MKFactionRegistry.getFaction(factionName);
        }
    }

    public void setFactionName(ResourceLocation factionName) {
        setFactionNameInternal(factionName);
        if (!getEntity().getEntityWorld().isRemote) {
            syncToAllTracking();
        }
    }

    public void syncToAllTracking() {
        MobFactionUpdatePacket updatePacket = new MobFactionUpdatePacket(this);
        PacketDistributor.TRACKING_ENTITY.with(this::getEntity)
                .send(PacketHandler.getNetworkChannel().toVanillaPacket(updatePacket, NetworkDirection.PLAY_TO_CLIENT));
    }

    public void attach(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    @Override
    public Targeting.TargetRelation getRelationToEntity(LivingEntity otherEntity) {
        MKFaction faction = getFaction();
        if (faction == null) {
            return Targeting.TargetRelation.UNHANDLED;
        }

        if (otherEntity instanceof PlayerEntity) {
            return otherEntity.getCapability(FactionCapabilities.PLAYER_FACTION_CAPABILITY)
                    .map(playerFaction -> playerFaction.getFactionRelation(factionName))
                    .orElse(Targeting.TargetRelation.UNHANDLED);
        }
        return otherEntity.getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY)
                .map(mobFaction -> faction.getNonPlayerEntityRelationship(otherEntity, mobFaction.getFactionName(), mobFaction.getFaction()))
                .orElse(Targeting.TargetRelation.UNHANDLED);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("factionName", getFactionName().toString());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("factionName")) {
            setFactionNameInternal(new ResourceLocation(nbt.getString("factionName")));
        } else {
            setFactionNameInternal(MKFaction.INVALID_FACTION);
        }
    }
}
