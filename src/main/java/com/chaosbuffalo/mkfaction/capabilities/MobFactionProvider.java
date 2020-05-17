package com.chaosbuffalo.mkfaction.capabilities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MobFactionProvider implements ICapabilitySerializable<CompoundNBT> {

    private final MobFactionHandler data;

    public MobFactionProvider(LivingEntity entity) {
        data = new MobFactionHandler();
        data.attach(entity);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return Capabilities.MOB_FACTION_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> data));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) Capabilities.MOB_FACTION_CAPABILITY.getStorage().writeNBT(
                Capabilities.MOB_FACTION_CAPABILITY, data, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        Capabilities.MOB_FACTION_CAPABILITY.getStorage().readNBT(
                Capabilities.MOB_FACTION_CAPABILITY, data, null, nbt);
    }
}