package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.mkfaction.MKFactionRegistry;
import com.chaosbuffalo.targeting_api.Targeting;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import static com.chaosbuffalo.mkfaction.capabilities.MobFactionHandler.INVALID_FACTION;


public class PlayerFactionEntry implements INBTSerializable<CompoundNBT> {

    private int factionScore;
    private final ResourceLocation factionName;
    public static final PlayerFactionEntry DEFAULT_ENTRY = new PlayerFactionEntry(INVALID_FACTION);

    public PlayerFactionEntry(ResourceLocation factionName){
        this.factionName = factionName;
        factionScore = 0;
    }

    public void setToDefaultFactionScore(){
        if (factionName.equals(INVALID_FACTION)){
            factionScore = 0;
        } else {
            Faction faction = MKFactionRegistry.getFaction(factionName);
            if (faction != null){
                factionScore = faction.getDefaultPlayerScore();
            } else {
                factionScore = 0;
            }
        }
    }

    public ResourceLocation getFactionName() {
        return factionName;
    }

    public int getFactionScore() {
        return factionScore;
    }

    public void setFactionScore(int factionScore) {
        this.factionScore = factionScore;
    }

    public void incrementFaction(int toAdd){
        factionScore += toAdd;
    }

    public void decrementFaction(int toSub){
        factionScore -= toSub;
    }

    public Targeting.TargetRelation getTargetRelationForFaction(){
        return FactionConstants.defaultTargetRelationFromFactionStatus(getFactionStatus());
    }

    public FactionConstants.PlayerFactionStatus getFactionStatus(){
        return FactionConstants.statusFromFactionAmount(getFactionScore());
    }


    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("factionScore", getFactionScore());
        return null;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("factionScore", 3)){
            setFactionScore(nbt.getInt("factionScore"));
        }
    }
}
