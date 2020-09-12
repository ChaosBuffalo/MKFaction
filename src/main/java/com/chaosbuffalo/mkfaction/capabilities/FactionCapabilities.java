package com.chaosbuffalo.mkfaction.capabilities;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

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
        CapabilityManager.INSTANCE.register(IPlayerFaction.class, new Storage<>(), PlayerFactionHandler::new);
        CapabilityManager.INSTANCE.register(IMobFaction.class, new Storage<>(), MobFactionHandler::new);
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
}
