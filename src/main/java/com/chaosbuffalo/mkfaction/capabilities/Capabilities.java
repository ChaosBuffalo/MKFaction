package com.chaosbuffalo.mkfaction.capabilities;

import com.chaosbuffalo.mkfaction.MKFaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Capabilities {

    public static ResourceLocation PLAYER_FACTION_CAP_ID = new ResourceLocation(MKFaction.MODID,
            "player_faction_data");
    public static ResourceLocation MOB_FACTION_CAP_ID = new ResourceLocation(MKFaction.MODID,
            "mob_faction_data");

    @CapabilityInject(IPlayerFaction.class)
    public static final Capability<IPlayerFaction> PLAYER_FACTION_CAPABILITY;

    @CapabilityInject(IMobFaction.class)
    public static final Capability<IMobFaction> MOB_FACTION_CAPABILITY;

    static {
        PLAYER_FACTION_CAPABILITY = null;
        MOB_FACTION_CAPABILITY = null;
    }

    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IPlayerFaction.class, new PlayerFactionHandler.Storage(),
                PlayerFactionHandler::new);
        CapabilityManager.INSTANCE.register(IMobFaction.class, new MobFactionHandler.Storage(),
                MobFactionHandler::new);
        MinecraftForge.EVENT_BUS.register(Capabilities.class);
    }

    @SubscribeEvent
    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof PlayerEntity) {
            e.addCapability(PLAYER_FACTION_CAP_ID, new PlayerFactionProvider((PlayerEntity) e.getObject()));
        } else if (e.getObject() instanceof LivingEntity){
            e.addCapability(MOB_FACTION_CAP_ID, new MobFactionProvider((LivingEntity) e.getObject()));
        }
    }
}
