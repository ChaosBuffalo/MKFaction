package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.mkcore.sync.IMKSerializable;
import com.chaosbuffalo.mkfaction.event.MKFactionRegistry;
import com.chaosbuffalo.targeting_api.Targeting;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

import static com.chaosbuffalo.mkfaction.faction.MKFaction.INVALID_FACTION;


public class PlayerFactionEntry implements IMKSerializable<CompoundNBT> {

    private int factionScore;
    private final ResourceLocation factionName;
    private PlayerFactionStatus factionStatus;
    private Consumer<PlayerFactionEntry> dirtyNotifier;

    public PlayerFactionEntry(ResourceLocation factionName) {
        this.factionName = factionName;
        setFactionScore(0);
    }

    public void setToDefaultFactionScore() {
        if (factionName.equals(INVALID_FACTION)) {
            setFactionScore(0);
        } else {
            MKFaction faction = MKFactionRegistry.getFaction(factionName);
            if (faction != null) {
                setFactionScore(faction.getDefaultPlayerScore());
            } else {
                setFactionScore(0);
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
        factionStatus = PlayerFactionStatus.forScore(factionScore);
        markDirty();
    }

    public void incrementFaction(int toAdd) {
        setFactionScore(factionScore + toAdd);
    }

    public void decrementFaction(int toSub) {
        setFactionScore(factionScore - toSub);
    }

    public Targeting.TargetRelation getTargetRelation() {
        return getFactionStatus().getRelation();
    }

    public PlayerFactionStatus getFactionStatus() {
        return factionStatus;
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("factionScore", getFactionScore());
        return tag;
    }

    @Override
    public boolean deserialize(CompoundNBT nbt) {
        if (nbt.contains("factionScore")) {
            setFactionScore(nbt.getInt("factionScore"));
        }
        return true;
    }

    public void setDirtyNotifier(Consumer<PlayerFactionEntry> notifier) {
        dirtyNotifier = notifier;
    }

    private void markDirty() {
        if (dirtyNotifier != null) {
            dirtyNotifier.accept(this);
        }
    }
}
