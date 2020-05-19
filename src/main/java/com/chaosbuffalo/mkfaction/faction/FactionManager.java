package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class FactionManager extends JsonReloadListener {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public FactionManager() {
        super(GSON, "factions");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> objectIn, IResourceManager resourceManagerIn,
                         IProfiler profilerIn) {
        MKFactionMod.LOGGER.info("In apply reload for FactionManager");
        for(Map.Entry<ResourceLocation, JsonObject> entry : objectIn.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            MKFactionMod.LOGGER.info("Found file: {}", resourcelocation);
            if (resourcelocation.getPath().startsWith("_")) continue; //Forge: filter anything beginning with "_" as it's used for metadata.
            parseFaction(entry.getKey(), entry.getValue());
        }
    }

    private void parseFaction(ResourceLocation loc, JsonObject json){
        MKFactionMod.LOGGER.info("Parsing Faction Json for {}");
    }
}
