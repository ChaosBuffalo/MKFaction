package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.targeting_api.Targeting;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;


public class PlayerFactionEntry implements INBTSerializable<CompoundNBT> {

    private int faction;
    public static final PlayerFactionEntry DEFAULT_ENTRY = new PlayerFactionEntry();

    public PlayerFactionEntry(){
        faction = 0;
    }

    public int getFaction() {
        return faction;
    }

    public void setFaction(int faction) {
        this.faction = faction;
    }

    public void incrementFaction(int toAdd){
        faction += toAdd;
    }

    public void decrementFaction(int toSub){
        faction -= toSub;
    }

    public Targeting.TargetRelation getTargetRelationForFaction(){
        return FactionConstants.defaultTargetRelationFromFactionStatus(getFactionStatus());
    }

    public FactionConstants.PlayerFactionStatus getFactionStatus(){
        return FactionConstants.statusFromFactionAmount(getFaction());
    }


    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("faction", getFaction());
        return null;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("faction", 3)){
            setFaction(nbt.getInt("faction"));
        }
    }
}
