package com.chaosbuffalo.mkfaction.event;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.mkfaction.faction.FactionDefaultManager;
import com.chaosbuffalo.mkfaction.faction.MKFaction;
import com.chaosbuffalo.mkfaction.network.MobFactionUpdatePacket;
import com.chaosbuffalo.mkfaction.network.PacketHandler;
import com.chaosbuffalo.mkfaction.network.PlayerFactionUpdatePacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid=MKFactionMod.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class MobHandlers {

    @SubscribeEvent
    public static void playerStartTracking(PlayerEvent.StartTracking event){
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getPlayer();
        event.getTarget().getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY).ifPresent(mobFaction -> {
            PacketDistributor.PLAYER.with(() -> serverPlayer).send(PacketHandler.getNetworkChannel().toVanillaPacket(
                    new MobFactionUpdatePacket(mobFaction), NetworkDirection.PLAY_TO_CLIENT));
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityJoinWorld(EntityJoinWorldEvent event){
        if (!event.getWorld().isRemote){
            if (event.getEntity() instanceof ServerPlayerEntity){
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getEntity();
                serverPlayer.getCapability(FactionCapabilities.PLAYER_FACTION_CAPABILITY).ifPresent(playerFaction ->
                        PacketDistributor.PLAYER.with(() -> serverPlayer).send(PacketHandler.getNetworkChannel()
                                .toVanillaPacket(new PlayerFactionUpdatePacket(playerFaction),
                                        NetworkDirection.PLAY_TO_CLIENT)));
            } else if (event.getEntity() instanceof LivingEntity){
                event.getEntity().getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY).ifPresent(mobFaction ->{
                    if (mobFaction.getFactionName().equals(MKFaction.INVALID_FACTION)){
                        mobFaction.setFactionName(FactionDefaultManager.getFactionForEntity(
                                event.getEntity().getType().getRegistryName()));
                    }
                });
            }
        }

    }
}
