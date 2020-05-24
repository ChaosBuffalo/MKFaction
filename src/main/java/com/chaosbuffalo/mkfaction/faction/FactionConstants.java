package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.targeting_api.Targeting;
import net.minecraft.util.text.TextFormatting;

public class FactionConstants {

    public static final int VILLAIN_THRESHOLD = -10000;
    public static final int HERO_THRESHOLD = 10000;
    public static final int FRIENDLY_THRESHOLD = 1000;
    public static final int WARY_THRESHOLD = -1000;
    public static final int ALLY_THRESHOLD = 5000;
    public static final int ENEMY_THRESHOLD = -5000;
    public static final int TRUE_NEUTRAL = 0;

    public enum PlayerFactionStatus {
        UNKNOWN,
        FRIEND,
        ENEMY,
        HERO,
        ALLY,
        VILLAIN,
        SUSPECT
    }

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
            return PlayerFactionStatus.HERO;
        } else if (factionAmount >= FactionConstants.ALLY_THRESHOLD){
            return PlayerFactionStatus.ALLY;
        } else if (factionAmount >= FactionConstants.FRIENDLY_THRESHOLD){
            return PlayerFactionStatus.FRIEND;
        } else if (factionAmount > FactionConstants.WARY_THRESHOLD) {
            return PlayerFactionStatus.UNKNOWN;
        } else if (factionAmount > FactionConstants.ENEMY_THRESHOLD){
            return PlayerFactionStatus.SUSPECT;
        } else if (factionAmount > FactionConstants.VILLAIN_THRESHOLD){
            return PlayerFactionStatus.ENEMY;
        } else {
            return PlayerFactionStatus.VILLAIN;
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
