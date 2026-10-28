package com.github.masyu.dismantle.recipe;

import com.github.masyu.dismantle.registries.ModRecipes;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/** 解体作業台専用のレシピ。入力1個 -> result + extras を出力グリッドに並べる。 */
public class DismantleRecipe implements Recipe<Container> {
    /** 出力グリッド(3x3)の最大スタック数 */
    public static final int MAX_OUTPUTS = 9;

    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack result;
    private final List<ItemStack> extras;

    public DismantleRecipe(ResourceLocation id, Ingredient input, ItemStack result, List<ItemStack> extras) {
        this.id = id;
        this.input = input;
        this.result = result;
        this.extras = extras;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access) {
        return result.copy();
    }

    /** 出力グリッドに並べるアイテム(result + extras) */
    public List<ItemStack> getOutputs() {
        List<ItemStack> list = new ArrayList<>();
        list.add(result.copy());
        for (ItemStack e : extras) {
            list.add(e.copy());
        }
        return list;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.DISMANTLE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.DISMANTLING_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<DismantleRecipe> {
        @Override
        public DismantleRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            List<ItemStack> extras = new ArrayList<>();
            if (json.has("extras")) {
                for (JsonElement e : GsonHelper.getAsJsonArray(json, "extras")) {
                    extras.add(ShapedRecipe.itemStackFromJson(GsonHelper.convertToJsonObject(e, "extras entry")));
                }
            }
            if (1 + extras.size() > MAX_OUTPUTS) {
                throw new JsonSyntaxException("Dismantle recipe " + id + " has more than " + MAX_OUTPUTS + " outputs");
            }
            return new DismantleRecipe(id, input, result, extras);
        }

        @Override
        public DismantleRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient input = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            int n = buf.readVarInt();
            List<ItemStack> extras = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                extras.add(buf.readItem());
            }
            return new DismantleRecipe(id, input, result, extras);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DismantleRecipe recipe) {
            recipe.input.toNetwork(buf);
            buf.writeItem(recipe.result);
            buf.writeVarInt(recipe.extras.size());
            for (ItemStack e : recipe.extras) {
                buf.writeItem(e);
            }
        }
    }
}
