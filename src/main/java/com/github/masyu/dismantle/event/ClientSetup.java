package com.github.masyu.dismantle.event;

import com.github.masyu.dismantle.client.DismantleScreen;
import com.github.masyu.dismantle.registries.ModMenus;
import com.github.masyu.dismantle.Dismantle;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Dismantle.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ModMenus.DISMANTLE.get(), DismantleScreen::new));
    }
}
