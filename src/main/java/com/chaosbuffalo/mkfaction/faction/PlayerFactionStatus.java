package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.targeting_api.Targeting;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.Comparator;

public enum PlayerFactionStatus {
    VILLAIN(FactionConstants.VILLAIN_THRESHOLD, "faction_status.villain", TextFormatting.DARK_RED, Targeting.TargetRelation.ENEMY),
    ENEMY(FactionConstants.ENEMY_THRESHOLD, "faction_status.enemy", TextFormatting.RED, Targeting.TargetRelation.ENEMY),
    UNKNOWN(FactionConstants.WARY_THRESHOLD, "faction_status.unknown", TextFormatting.GRAY, Targeting.TargetRelation.NEUTRAL),
    SUSPECT(FactionConstants.TRUE_NEUTRAL, "faction_status.suspect", TextFormatting.DARK_GRAY, Targeting.TargetRelation.NEUTRAL),
    FRIEND(FactionConstants.FRIENDLY_THRESHOLD, "faction_status.friend", TextFormatting.AQUA, Targeting.TargetRelation.NEUTRAL),
    ALLY(FactionConstants.ALLY_THRESHOLD, "faction_status.ally", TextFormatting.GREEN, Targeting.TargetRelation.FRIEND),
    HERO(FactionConstants.HERO_THRESHOLD, "faction_status.hero", TextFormatting.GOLD, Targeting.TargetRelation.FRIEND);

    private static final PlayerFactionStatus[] sortedStatus = Arrays.stream(values())
            .sorted(Comparator.comparingInt(PlayerFactionStatus::getThreshold).reversed())
            .toArray(PlayerFactionStatus[]::new);

    final int threshold;
    final String translationKey;
    final TextFormatting color;
    final Targeting.TargetRelation relation;

    PlayerFactionStatus(int value, String translationKey, TextFormatting color, Targeting.TargetRelation relation) {
        this.threshold = value;
        this.translationKey = translationKey;
        this.color = color;
        this.relation = relation;
    }

    public int getThreshold() {
        return threshold;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public TextFormatting getColor() {
        return color;
    }

    public Targeting.TargetRelation getRelation() {
        return relation;
    }

    public static PlayerFactionStatus forScore(int factionAmount) {
        for (PlayerFactionStatus playerFactionStatus : sortedStatus) {
            if (factionAmount >= playerFactionStatus.getThreshold()) {
                return playerFactionStatus;
            }
        }
        return VILLAIN;
    }
}
