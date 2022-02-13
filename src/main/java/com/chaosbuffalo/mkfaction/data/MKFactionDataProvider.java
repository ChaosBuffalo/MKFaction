package com.chaosbuffalo.mkfaction.data;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.faction.FactionManager;
import com.chaosbuffalo.mkfaction.faction.MKFaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class MKFactionDataProvider implements IDataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String modId;
    private final DataGenerator generator;

    public MKFactionDataProvider(String modId, DataGenerator generator) {
        this.modId = modId;
        this.generator = generator;
    }

    @Override
    public abstract void act(@Nonnull DirectoryCache cache) throws IOException;

    public void writeFaction(MKFaction faction, @Nonnull DirectoryCache cache) {
        Path outputFolder = generator.getOutputFolder();
        ResourceLocation key = Objects.requireNonNull(faction.getRegistryName());
        Path local = Paths.get("data", key.getNamespace(), FactionManager.DEFINITION_FOLDER, key.getPath() + ".json");
        Path path = outputFolder.resolve(local);
        try {
            JsonElement element = faction.serialize(JsonOps.INSTANCE);
            IDataProvider.save(GSON, cache, element, path);
        } catch (IOException exception) {
            MKFactionMod.LOGGER.error("Couldn't write faction {}", path, exception);
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return String.format("Faction Generator for %s", modId);
    }
}
