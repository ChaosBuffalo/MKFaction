package com.chaosbuffalo.mkfaction;

import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.targeting_api.Targeting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class TargetingHooks {

    private static Targeting.TargetRelation targetHook(Entity source, Entity target){
        if (source instanceof PlayerEntity && target instanceof PlayerEntity){
            return Targeting.TargetRelation.UNHANDLED;
        } else if (source instanceof PlayerEntity && target instanceof LivingEntity){
            return source.getCapability(FactionCapabilities.PLAYER_FACTION_CAPABILITY).map(
                    (playerFaction) -> target.getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY).map(
                            (mobFaction) -> playerFaction.getFactionEntry(mobFaction.getFactionName())
                                    .getTargetRelationForFaction())
                            .orElse(Targeting.TargetRelation.UNHANDLED))
                    .orElse(Targeting.TargetRelation.UNHANDLED);
        } else if (source instanceof LivingEntity && target instanceof PlayerEntity){
            return target.getCapability(FactionCapabilities.PLAYER_FACTION_CAPABILITY).map(
                    (playerFaction) -> source.getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY).map(
                            (mobFaction) -> playerFaction.getFactionEntry(mobFaction.getFactionName())
                                    .getTargetRelationForFaction())
                            .orElse(Targeting.TargetRelation.UNHANDLED))
                    .orElse(Targeting.TargetRelation.UNHANDLED);
        } else if (source instanceof LivingEntity && target instanceof LivingEntity){
            return source.getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY)
                    .map((sourceFaction) -> sourceFaction.getRelationToMob((LivingEntity) target))
                    .orElse(Targeting.TargetRelation.UNHANDLED);
        }
        return Targeting.TargetRelation.UNHANDLED;
    }

    public static void registerHooks() {
        Targeting.registerRelationCallback(TargetingHooks::targetHook);
    }
}
