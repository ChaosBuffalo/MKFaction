package com.chaosbuffalo.mkfaction.capabilities;

import com.chaosbuffalo.mkfaction.faction.PlayerFactionEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.HashMap;

public class PlayerFactionHandler implements IPlayerFaction {

    private final HashMap<ResourceLocation, PlayerFactionEntry> factionMap;
    private PlayerEntity player;

    public PlayerFactionHandler(){
        this.factionMap = new HashMap<>();
    }

    @Override
    public HashMap<ResourceLocation, PlayerFactionEntry> getFactionMap() {
        return factionMap;
    }

    @Override
    public void attach(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public PlayerEntity getPlayer() {
        return player;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        CompoundNBT factions = new CompoundNBT();
        for (ResourceLocation key : getFactionMap().keySet()){
            PlayerFactionEntry entry = getFactionMap().get(key);
            factions.put(key.toString(), entry.serializeNBT());
        }
        tag.put("factions", factions);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("factions")){
            CompoundNBT factionsTag = nbt.getCompound("factions");
            for (String key : factionsTag.keySet()){
                ResourceLocation factionName = new ResourceLocation(key);
                PlayerFactionEntry newEntry = new PlayerFactionEntry(factionName);
                newEntry.deserializeNBT(factionsTag.getCompound(key));
                factionMap.put(factionName, newEntry);
            }
        }
    }

    public static class Storage implements Capability.IStorage<IPlayerFaction> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<IPlayerFaction> capability, IPlayerFaction instance, Direction side) {
            if (instance == null){
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IPlayerFaction> capability, IPlayerFaction instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT && instance != null) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }
}
