package com.github.masyu.dismantle.data;

import com.github.masyu.dismantle.network.ModNetwork;
import com.github.masyu.dismantle.network.SyncDismantlePacket;
import com.github.masyu.dismantle.Dismantle;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = Dismantle.MODID)
public class DismantleEvents {

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(DismantleManager.INSTANCE);
    }

    /** ログイン時と /reload 後に、データをクライアントへ送る */
    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        SyncDismantlePacket packet = new SyncDismantlePacket(new ArrayList<>(DismantleManager.INSTANCE.getEntries()));
        event.getPlayers().forEach(player ->
                ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet));
    }
}
