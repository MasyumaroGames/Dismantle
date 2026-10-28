package com.github.masyu.dismantle.jei;

import com.github.masyu.dismantle.data.DismantleEntry;
import com.github.masyu.dismantle.data.DismantleManager;
import com.github.masyu.dismantle.registries.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    public static final RecipeType<DismantleEntry> DISMANTLE_TYPE =
            RecipeType.create("dismantle", "dismantling", DismantleEntry.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("dismantle", "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        reg.addRecipeCategories(new DismantleCategory(reg.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        reg.addRecipes(DISMANTLE_TYPE, new ArrayList<>(DismantleManager.INSTANCE.all()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(new ItemStack(ModBlocks.Blocks.DISMANTLE_TABLE.get()), DISMANTLE_TYPE);
    }
}
