package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.targeting_api.Targeting;
import net.minecraft.util.text.TextFormatting;

public enum PlayerFactionStatus {
    UNKNOWN,
    FRIEND,
    ENEMY,
    HERO,
    ALLY,
    VILLAIN,
    SUSPECT;

    public static String translationKeyFromFactionStatus(PlayerFactionStatus status){
        switch (status){
            case FRIEND:
                return "faction_status.friend";
            case ENEMY:
                return "faction_status.enemy";
            case HERO:
                return "faction_status.hero";
            case ALLY:
                return "faction_status.ally";
            case VILLAIN:
                return "faction_status.villain";
            case SUSPECT:
                return "faction_status.suspect";
            case UNKNOWN:
            default:
                return "faction_status.unknown";
        }
    }

    public static TextFormatting colorForFactionStatus(PlayerFactionStatus status){
        switch (status){
            case FRIEND:
                return TextFormatting.AQUA;
            case ENEMY:
                return TextFormatting.RED;
            case HERO:
                return TextFormatting.GOLD;
            case ALLY:
                return TextFormatting.GREEN;
            case VILLAIN:
                return TextFormatting.DARK_RED;
            case SUSPECT:
                return TextFormatting.DARK_GRAY;
            case UNKNOWN:
            default:
                return TextFormatting.GRAY;
        }
    }

    public static PlayerFactionStatus statusFromFactionAmount(int factionAmount){
        if (factionAmount >= FactionConstants.HERO_THRESHOLD){
            return HERO;
        } else if (factionAmount >= FactionConstants.ALLY_THRESHOLD){
            return ALLY;
        } else if (factionAmount >= FactionConstants.FRIENDLY_THRESHOLD){
            return FRIEND;
        } else if (factionAmount > FactionConstants.WARY_THRESHOLD) {
            return UNKNOWN;
        } else if (factionAmount > FactionConstants.ENEMY_THRESHOLD){
            return SUSPECT;
        } else if (factionAmount > FactionConstants.VILLAIN_THRESHOLD){
            return ENEMY;
        } else {
            return VILLAIN;
        }
    }

    public static Targeting.TargetRelation defaultTargetRelationFromFactionStatus(PlayerFactionStatus status){
        switch (status){
            case ALLY:
            case HERO:
                return Targeting.TargetRelation.FRIEND;
            case ENEMY:
            case VILLAIN:
                return Targeting.TargetRelation.ENEMY;
            default:
                return Targeting.TargetRelation.NEUTRAL;
        }
    }
}
