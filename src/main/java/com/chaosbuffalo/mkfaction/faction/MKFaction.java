package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.capabilities.Capabilities;
import com.chaosbuffalo.targeting_api.Targeting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class MKFaction implements IForgeRegistryEntry<MKFaction> {
    public static final ResourceLocation INVALID_FACTION = new ResourceLocation(MKFactionMod.MODID,
            "faction.invalid");
    private ResourceLocation name;
    private final Set<ResourceLocation> allies;
    private final Set<ResourceLocation> enemies;
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
            return entity.getCapability(Capabilities.MOB_FACTION_CAPABILITY)
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
