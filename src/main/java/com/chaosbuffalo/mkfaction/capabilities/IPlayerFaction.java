package com.chaosbuffalo.mkfaction.capabilities;

import com.chaosbuffalo.mkfaction.faction.PlayerFactionEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;

public interface IPlayerFaction extends INBTSerializable<CompoundNBT> {

    HashMap<ResourceLocation, PlayerFactionEntry> getFactionMap();

    void attach(PlayerEntity player);

    default PlayerFactionEntry getFactionEntry(ResourceLocation factionName){
        HashMap<ResourceLocation, PlayerFactionEntry> factions = getFactionMap();
        if (!factions.containsKey(factionName)){
            factions.put(factionName, new PlayerFactionEntry());
        }
        return factions.getOrDefault(factionName, PlayerFactionEntry.DEFAULT_ENTRY);
    }

    default void addRepToFaction(ResourceLocation factionName, int factionAmount){
        PlayerFactionEntry factionEntry = getFactionEntry(factionName);
        factionEntry.incrementFaction(factionAmount);
    }

    default void subRepFromFaction(ResourceLocation factionName, int factionAmount){
        PlayerFactionEntry factionEntry = getFactionEntry(factionName);
        factionEntry.decrementFaction(factionAmount);
    }
}
