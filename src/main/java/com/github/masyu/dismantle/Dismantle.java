package com.github.masyu.dismantle;

import com.github.masyu.dismantle.registries.ModBlocks;
import com.github.masyu.dismantle.registries.ModMenus;
import com.github.masyu.dismantle.registries.ModRecipes;
import com.github.masyu.dismantle.registries.ModTabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;

@Mod("dismantle")
public class Dismantle {

    public static final String MODID = "dismantle";

    public Dismantle()
    {
        IEventBus Bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.Blocks.BLOCKS.register(Bus);
        ModBlocks.BlockItems.BLOCKS_ITEMS.register(Bus);
        ModRecipes.SERIALIZERS.register(Bus);
        ModRecipes.TYPES.register(Bus);
        ModMenus.MENUS.register(Bus);
        ModTabs.MOD_TABS.register(Bus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, com.github.masyu.dismantle.registries.ModConfig.SPEC);

        Bus.addListener(this::addCreative);
    }

    public static ResourceLocation id(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath("dismantle", path);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.BlockItems.DISMANTLE_TABLE);
        }
    }
}
