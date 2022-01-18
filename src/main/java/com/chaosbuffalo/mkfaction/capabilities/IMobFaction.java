package com.chaosbuffalo.mkfaction.capabilities;

import com.chaosbuffalo.mkfaction.faction.MKFaction;
import com.chaosbuffalo.targeting_api.Targeting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public interface IMobFaction extends INBTSerializable<CompoundNBT> {

    ResourceLocation getFactionName();

    void setFactionName(ResourceLocation factionName);

    MKFaction getFaction();

    Targeting.TargetRelation getRelationToEntity(LivingEntity entity);

    LivingEntity getEntity();

}
