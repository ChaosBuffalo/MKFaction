package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.targeting_api.Targeting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.HashSet;
import java.util.Set;

public class MKFaction extends ForgeRegistryEntry<MKFaction> {
    public static final ResourceLocation INVALID_FACTION = new ResourceLocation(MKFactionMod.MODID, "faction.invalid");
    private final Set<ResourceLocation> allies;
    private final Set<ResourceLocation> enemies;
    private final Set<String> firstNames;
    private final Set<String> lastNames;
    private int defaultPlayerScore;

    public MKFaction(ResourceLocation name, int defaultPlayerScore){
        this(name, defaultPlayerScore, new HashSet<>(), new HashSet<>());
    }

    public MKFaction(ResourceLocation name, int defaultPlayerScore, Set<ResourceLocation> allies,
                     Set<ResourceLocation> enemies){
        setRegistryName(name);
        this.allies = allies;
        this.enemies = enemies;
        this.defaultPlayerScore = defaultPlayerScore;
        this.firstNames = new HashSet<>();
        this.lastNames = new HashSet<>();
    }

    public String getTranslationKey(){
        if (getRegistryName() != null){
            return String.format("faction.%s.%s.name", getRegistryName().getNamespace(), getRegistryName().getPath());
        } else {
            return "faction.mkfaction.invalid.name";
        }
    }

    public Set<String> getFirstNames() {
        return firstNames;
    }

    public Set<String> getLastNames() {
        return lastNames;
    }

    public void addFirstName(String name){
        firstNames.add(name);
    }

    public void addLastName(String name){
        lastNames.add(name);
    }

    public int getDefaultPlayerScore(){
        return defaultPlayerScore;
    }

    public void setDefaultPlayerScore(int defaultPlayerScore) {
        this.defaultPlayerScore = defaultPlayerScore;
    }

    public void clearAllies(){
        allies.clear();
    }

    public void clearEnemies(){
        enemies.clear();
    }

    public Set<ResourceLocation> getAllies() {
        return allies;
    }

    public Set<ResourceLocation> getEnemies() {
        return enemies;
    }

    public void addAlly(ResourceLocation allyName){
        allies.add(allyName);
    }

    public void addEnemy(ResourceLocation enemyName){
        enemies.add(enemyName);
    }

    public boolean isEnemy(ResourceLocation faction){
        return enemies.contains(faction);
    }

    public boolean isAlly(ResourceLocation faction){
        return allies.contains(faction);
    }

    public boolean isMember(LivingEntity entity){
        if (entity instanceof PlayerEntity){
            return false;
        } else {
            return entity.getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY)
                    .map((cap) -> cap.getFactionName().equals(getRegistryName())).orElse(false);
        }
    }

    public Targeting.TargetRelation getNonPlayerEntityRelationship(LivingEntity entity, ResourceLocation factionName){
        if (isMember(entity)){
            return Targeting.TargetRelation.FRIEND;
        } else if (isEnemy(factionName)){
            return Targeting.TargetRelation.ENEMY;
        } else if (isAlly(factionName)){
            return Targeting.TargetRelation.FRIEND;
        } else {
            return Targeting.TargetRelation.NEUTRAL;
        }
    }
}
