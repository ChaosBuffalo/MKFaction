package com.chaosbuffalo.mkfaction.capabilities;

import com.chaosbuffalo.mkfaction.faction.PlayerFactionEntry;
import com.chaosbuffalo.mkfaction.faction.PlayerFactionStatus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;

public interface IPlayerFaction extends INBTSerializable<CompoundNBT> {

    HashMap<ResourceLocation, PlayerFactionEntry> getFactionMap();

    void attach(PlayerEntity player);

    PlayerEntity getPlayer();

    default PlayerFactionStatus getFactionStatus(ResourceLocation factionName){
        return getFactionEntry(factionName).getFactionStatus();
    }

    default PlayerFactionEntry getFactionEntry(ResourceLocation factionName){
        HashMap<ResourceLocation, PlayerFactionEntry> factions = getFactionMap();
        if (!factions.containsKey(factionName)){
            PlayerFactionEntry newEntry = new PlayerFactionEntry(factionName);
            newEntry.setToDefaultFactionScore();
            factions.put(factionName, newEntry);
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
