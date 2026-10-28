package com.github.masyu.dismantle.network;

import com.github.masyu.dismantle.Dismantle;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL = "1";
    public static SimpleChannel CHANNEL;   // final を外す

    public static void register() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Dismantle.MODID, "main"),
                () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
        int id = 0;
        CHANNEL.registerMessage(0, SyncDismantlePacket.class,
                SyncDismantlePacket::encode, SyncDismantlePacket::decode, SyncDismantlePacket::handle);
    }
}
