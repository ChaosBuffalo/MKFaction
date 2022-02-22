package com.chaosbuffalo.mkfaction.capabilities;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FactionCapabilities {

    public static ResourceLocation PLAYER_FACTION_CAP_ID = new ResourceLocation(MKFactionMod.MODID,
            "player_faction_data");
    public static ResourceLocation MOB_FACTION_CAP_ID = new ResourceLocation(MKFactionMod.MODID,
            "mob_faction_data");

    @CapabilityInject(IPlayerFaction.class)
    public static final Capability<IPlayerFaction> PLAYER_FACTION_CAPABILITY;

    @CapabilityInject(IMobFaction.class)
    public static final Capability<IMobFaction> MOB_FACTION_CAPABILITY;

    static {
        PLAYER_FACTION_CAPABILITY = null;
        MOB_FACTION_CAPABILITY = null;
    }

    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IPlayerFaction.class, new Storage<>(), () -> null);
        CapabilityManager.INSTANCE.register(IMobFaction.class, new Storage<>(), () -> null);
    }

    public static class Storage<T extends INBTSerializable<CompoundNBT>> implements Capability.IStorage<T> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
            if (instance == null) {
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT && instance != null) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }

    public abstract static class Provider<CapTarget, CapType extends INBTSerializable<CompoundNBT>>
            implements ICapabilitySerializable<CompoundNBT> {

        private final CapType data;
        private final LazyOptional<CapType> capOpt;

        public Provider(CapTarget attached) {
            data = makeData(attached);
            capOpt = LazyOptional.of(() -> data);
        }

        abstract CapType makeData(CapTarget attached);

        abstract Capability<CapType> getCapability();

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return getCapability().orEmpty(cap, capOpt);
        }

        public void invalidate() {
            capOpt.invalidate();
        }

        @Override
        public CompoundNBT serializeNBT() {
            return data.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            data.deserializeNBT(nbt);
        }
    }
}
