package com.chaosbuffalo.mkfaction.capabilities;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.MKFactionRegistry;
import com.chaosbuffalo.mkfaction.faction.MKFaction;
import com.chaosbuffalo.targeting_api.Targeting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class MobFactionHandler implements IMobFaction {
    private ResourceLocation factionName;
    private MKFaction faction;
    public static final ResourceLocation INVALID_FACTION = new ResourceLocation(MKFactionMod.MODID,
            "faction.invalid");
    private LivingEntity entity;

    public MobFactionHandler(){
        factionName = INVALID_FACTION;
        entity = null;
    }

    public MKFaction getFaction() {
        return faction;
    }

    @Override
    public ResourceLocation getFactionName() {
        return factionName;
    }

    public void setFactionName(ResourceLocation factionName) {
        this.factionName = factionName;
    }

    @Override
    public void attach(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public Targeting.TargetRelation getRelationToMob(LivingEntity entity) {
        if (faction == null){
            return Targeting.TargetRelation.UNHANDLED;
        }
        return entity.getCapability(Capabilities.MOB_FACTION_CAPABILITY).map((faction) ->
                getFaction().getEntityRelationship(entity, faction.getFactionName()))
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
        factionName = new ResourceLocation(nbt.getString("factionName"));
        this.faction = MKFactionRegistry.FACTION_REGISTRY.getValue(factionName);
    }

    public static class Storage implements Capability.IStorage<IMobFaction> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<IMobFaction> capability, IMobFaction instance, Direction side) {
            if (instance == null){
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IMobFaction> capability, IMobFaction instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT && instance != null) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }
}
