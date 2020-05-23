package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.targeting_api.Targeting;

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
