package com.chaosbuffalo.mkfaction.capabilities;

import com.chaosbuffalo.mkfaction.faction.PlayerFactionEntry;
import com.chaosbuffalo.mkfaction.faction.PlayerFactionStatus;
import com.chaosbuffalo.targeting_api.Targeting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.Optional;

public interface IPlayerFaction extends INBTSerializable<CompoundNBT> {

    PlayerEntity getPlayer();

    Map<ResourceLocation, PlayerFactionEntry> getFactionMap();

    Optional<PlayerFactionEntry> getFactionEntry(ResourceLocation factionName);

    default PlayerFactionStatus getFactionStatus(ResourceLocation factionName) {
        return getFactionEntry(factionName)
                .map(PlayerFactionEntry::getFactionStatus)
                .orElse(PlayerFactionStatus.UNKNOWN);
    }

    default Targeting.TargetRelation getFactionRelation(ResourceLocation factionName) {
        return getFactionEntry(factionName)
                .map(PlayerFactionEntry::getTargetRelation)
                .orElse(Targeting.TargetRelation.UNHANDLED);
    }

    default void addRepToFaction(ResourceLocation factionName, int factionAmount) {
        getFactionEntry(factionName).ifPresent(entry -> entry.incrementFaction(factionAmount));
    }

    default void subRepFromFaction(ResourceLocation factionName, int factionAmount) {
        getFactionEntry(factionName).ifPresent(entry -> entry.decrementFaction(factionAmount));
    }
}
