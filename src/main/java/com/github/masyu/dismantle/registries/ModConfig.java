package com.github.masyu.dismantle.registries;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLE_BUILTIN_VANILLA_RECIPES;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("dismantle");
        ENABLE_BUILTIN_VANILLA_RECIPES = builder
                .comment("If true, the vanilla-item dismantling recipes bundled with this mod",
                        "(data/<modid>/dismantling/vanilla/) are enabled.",
                        "Recipes added by datapacks or other mods are not affected by this setting.")
                .define("enableBuiltInVanillaRecipes", true);
        builder.pop();
        SPEC = builder.build();
    }

    /** 設定が読み込まれる前に呼ばれた場合は、有効(true)として扱う */
    public static boolean enableBuiltInVanillaRecipes() {
        try {
            return ENABLE_BUILTIN_VANILLA_RECIPES.get();
        } catch (IllegalStateException e) {
            return true;
        }
    }
}
