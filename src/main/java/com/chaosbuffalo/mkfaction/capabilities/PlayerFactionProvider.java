package com.chaosbuffalo.mkfaction.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerFactionProvider implements ICapabilitySerializable<CompoundNBT> {

    private final PlayerFactionHandler data;

    public PlayerFactionProvider(PlayerEntity player){
        data = new PlayerFactionHandler();
        data.attach(player);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return Capabilities.PLAYER_FACTION_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> data));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) Capabilities.PLAYER_FACTION_CAPABILITY.getStorage().writeNBT(
                Capabilities.PLAYER_FACTION_CAPABILITY, data, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        Capabilities.PLAYER_FACTION_CAPABILITY.getStorage().readNBT(
                Capabilities.PLAYER_FACTION_CAPABILITY, data, null, nbt);
    }


}
