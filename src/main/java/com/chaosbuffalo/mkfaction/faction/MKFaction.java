package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.targeting_api.Targeting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class MKFaction implements IForgeRegistryEntry<MKFaction> {
    private ResourceLocation name;
    private final Set<ResourceLocation> allies;
    private final Set<ResourceLocation> enemies;
    private int defaultPlayerScore;

    public MKFaction(ResourceLocation name, int defaultPlayerScore){
        setRegistryName(name);
        allies = new HashSet<>();
        enemies = new HashSet<>();
        this.defaultPlayerScore = defaultPlayerScore;
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

    public Targeting.TargetRelation getNonPlayerEntityRelationship(LivingEntity entity, ResourceLocation factionName){
        if (isEnemy(factionName)){
            return Targeting.TargetRelation.ENEMY;
        } else if (isAlly(factionName)){
            return Targeting.TargetRelation.FRIEND;
        } else {
            return Targeting.TargetRelation.NEUTRAL;
        }
    }

    @Override
    public MKFaction setRegistryName(ResourceLocation name) {
        this.name = name;
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return name;
    }

    @Override
    public Class<MKFaction> getRegistryType() {
        return MKFaction.class;
    }
}
