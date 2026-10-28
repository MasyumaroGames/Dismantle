package com.github.masyu.dismantle.registries;

import com.github.masyu.dismantle.menu.DismantleMenu;
import com.github.masyu.dismantle.Dismantle;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Dismantle.MODID);

    public static final RegistryObject<MenuType<DismantleMenu>> DISMANTLE = MENUS.register("dismantle",
            () -> new MenuType<DismantleMenu>(DismantleMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
