package com.chaosbuffalo.mkfaction.faction;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.util.SingleJsonFileReloadListener;
import com.google.gson.*;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class FactionDefaultManager extends SingleJsonFileReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static HashMap<ResourceLocation, ResourceLocation> factionDefaults = new HashMap<>();

    public FactionDefaultManager(){
        super(GSON, MKFactionMod.MODID, "categories");
    }

    public static ResourceLocation getFactionForEntity(ResourceLocation entity){
        return factionDefaults.getOrDefault(entity, MKFaction.INVALID_FACTION);
    }

    @Override
    protected void apply(JsonObject objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        JsonArray arr = objectIn.getAsJsonArray("members");
        factionDefaults.clear();
        for (JsonElement ele : arr){
            JsonObject obj = ele.getAsJsonObject();
            ResourceLocation factionName = new ResourceLocation(obj.get("name").getAsString());
            JsonArray members = obj.getAsJsonArray("defaultMembers");
            for (JsonElement memb : members){
                ResourceLocation memberName = new ResourceLocation(memb.getAsString());
                factionDefaults.put(memberName, factionName);
            }
        }
    }
}
