package com.chaosbuffalo.mkfaction;

import com.chaosbuffalo.mkfaction.faction.Faction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class MKFactionRegistry {
    public static IForgeRegistry<Faction> FACTION_REGISTRY = null;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void createRegistries(RegistryEvent.NewRegistry event) {
        FACTION_REGISTRY = new RegistryBuilder<Faction>()
                .setName(new ResourceLocation(MKFaction.MODID, "factions"))
                .setType(Faction.class)
                .setIDRange(0, Integer.MAX_VALUE - 1)
                .allowModification()
                .create();
    }
}
