package com.chaosbuffalo.mkfaction;

import com.chaosbuffalo.mkfaction.faction.MKFaction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class MKFactionRegistry {
    public static IForgeRegistry<MKFaction> FACTION_REGISTRY = null;

    @Nullable
    public static MKFaction getFaction(ResourceLocation name){
        return FACTION_REGISTRY.getValue(name);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void createRegistries(RegistryEvent.NewRegistry event) {
        FACTION_REGISTRY = new RegistryBuilder<MKFaction>()
                .setName(new ResourceLocation(MKFactionMod.MODID, "factions"))
                .setType(MKFaction.class)
                .setIDRange(0, Integer.MAX_VALUE - 1)
                .allowModification()
                .create();
    }
}
