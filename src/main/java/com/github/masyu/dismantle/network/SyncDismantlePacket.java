package com.github.masyu.dismantle.network;

import com.github.masyu.dismantle.data.DismantleEntry;
import com.github.masyu.dismantle.data.DismantleManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncDismantlePacket {
    private final List<DismantleEntry> entries;

    public SyncDismantlePacket(List<DismantleEntry> entries) {
        this.entries = entries;
    }

    public static void encode(SyncDismantlePacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.entries.size());
        for (DismantleEntry e : packet.entries) {
            e.toNetwork(buf);
        }
    }

    public static SyncDismantlePacket decode(FriendlyByteBuf buf) {
        int n = buf.readVarInt();
        List<DismantleEntry> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(DismantleEntry.fromNetwork(buf));
        }
        return new SyncDismantlePacket(list);
    }

    public static void handle(SyncDismantlePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DismantleManager.INSTANCE.setEntries(packet.entries));
        ctx.get().setPacketHandled(true);
    }
}
