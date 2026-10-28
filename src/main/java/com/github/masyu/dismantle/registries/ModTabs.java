package com.github.masyu.dismantle.registries;

import com.github.masyu.dismantle.tab.MainTab;
import com.github.masyu.dismantle.Dismantle;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {

    public static final DeferredRegister<CreativeModeTab> MOD_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Dismantle.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = MOD_TABS.register("main_tab",
            ()->{return CreativeModeTab.builder()
                    .icon(()->new ItemStack(ModBlocks.BlockItems.DISMANTLE_TABLE.get()))
                    .title(Component.translatable("itemGroup.main_tab"))
                    .displayItems((param, output) -> {
                        for (Item item : MainTab.items) {
                            output.accept(item);
                        }
                    })
                    .build();
            });

}
