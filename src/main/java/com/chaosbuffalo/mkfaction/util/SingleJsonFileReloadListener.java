package com.chaosbuffalo.mkfaction.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.ReloadListener;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class SingleJsonFileReloadListener extends ReloadListener<JsonObject> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Gson gson;
    private final ResourceLocation loc;

    public SingleJsonFileReloadListener(Gson gson, String modid, String path){
        this.gson = gson;
        this.loc = new ResourceLocation(modid, path + ".json");
    }

    public ResourceLocation getLoc() {
        return loc;
    }

    @Override
    protected JsonObject prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
        try (
                IResource iresource = resourceManagerIn.getResource(getLoc());
                InputStream inputstream = iresource.getInputStream();
                Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
        ) {
            JsonObject jsonobject = JSONUtils.fromJson(this.gson, reader, JsonObject.class);
            if (jsonobject != null) {
                return jsonobject;
            } else {
                LOGGER.error("Couldn't load data file {} as it's null or empty", loc);
            }
        } catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
            LOGGER.error("Couldn't parse data file {}", loc, jsonparseexception);
        }
        return null;
    }
}