package com.chaosbuffalo.mkfaction.event;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.mkfaction.capabilities.MobFactionProvider;
import com.chaosbuffalo.mkfaction.capabilities.PlayerFactionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid=MKFactionMod.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof PlayerEntity) {
            e.addCapability(FactionCapabilities.PLAYER_FACTION_CAP_ID, new PlayerFactionProvider((PlayerEntity) e.getObject()));
        } else if (e.getObject() instanceof LivingEntity){
            e.addCapability(FactionCapabilities.MOB_FACTION_CAP_ID, new MobFactionProvider((LivingEntity) e.getObject()));
        }
    }
}
