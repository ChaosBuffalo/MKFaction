package com.chaosbuffalo.mkfaction.capabilities;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class Capabilities {

    public static ResourceLocation PLAYER_FACTION_CAP_ID = new ResourceLocation(MKFactionMod.MODID,
            "player_faction_data");
    public static ResourceLocation MOB_FACTION_CAP_ID = new ResourceLocation(MKFactionMod.MODID,
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
    }


}
