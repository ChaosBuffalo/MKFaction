package com.chaosbuffalo.mkfaction.network;

import com.chaosbuffalo.mkfaction.MKFactionMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

    private static SimpleChannel networkChannel;
    private static final String VERSION = "1.0";

    public static void setupHandler(){
        networkChannel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(MKFactionMod.MODID, "packet_handler"),
                () -> VERSION, s -> s.equals(VERSION), s -> s.equals(VERSION));
        registerMessages(networkChannel);
    }

    public static SimpleChannel getNetworkChannel(){
        return networkChannel;
    }


    public static void registerMessages(SimpleChannel channel){
        channel.registerMessage(1, MKFactionUpdatePacket.class,
                MKFactionUpdatePacket::toBytes,
                MKFactionUpdatePacket::new,
                MKFactionUpdatePacket::handle);
        channel.registerMessage(2, MobFactionUpdatePacket.class,
                MobFactionUpdatePacket::toBytes,
                MobFactionUpdatePacket::new,
                MobFactionUpdatePacket::handle);
    }
}