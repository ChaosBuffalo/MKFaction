package com.chaosbuffalo.mkfaction.init;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.faction.MKFaction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class Faction {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerFactions(RegistryEvent.Register<MKFaction> event) {
        event.getRegistry().register(new MKFaction(
                new ResourceLocation(MKFactionMod.MODID, "test"), 100));
    }
}
