package com.github.masyu.dismantle.jei;

import com.github.masyu.dismantle.data.DismantleEntry;
import com.github.masyu.dismantle.registries.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DismantleCategory implements IRecipeCategory<DismantleEntry> {
    private final IDrawable background;
    private final IDrawable icon;

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("dismantle", "textures/gui/jei_dismantle.png");

    public DismantleCategory(IGuiHelper gui) {
        this.background = gui.drawableBuilder(TEXTURE, 0, 0, 141, 73)
                .setTextureSize(141, 73)
                .build();
        this.icon = gui.createDrawableItemStack(new ItemStack(ModBlocks.Blocks.DISMANTLE_TABLE.get()));
    }

    @Override public RecipeType<DismantleEntry> getRecipeType() { return ModJeiPlugin.DISMANTLE_TYPE; }
    @Override public Component getTitle() { return Component.translatable("jei.dismantle.dismantling"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DismantleEntry e, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 15, 28)
                .addIngredients(e.input());

        List<ItemStack> outs = e.outputs();
        for (int i = 0; i < outs.size() && i < 9; i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 77 + (i % 3) * 18, 10 + (i / 3) * 18)
                    .addItemStack(outs.get(i));
        }
    }
}
