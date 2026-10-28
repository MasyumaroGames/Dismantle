package com.github.masyu.dismantle.registries;

import com.github.masyu.dismantle.Dismantle;
import com.github.masyu.dismantle.recipe.DismantleRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Dismantle.MODID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Dismantle.MODID);

    public static final RegistryObject<RecipeSerializer<DismantleRecipe>> DISMANTLE =
            SERIALIZERS.register("dismantle", DismantleRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<DismantleRecipe>> DISMANTLING_TYPE =
            TYPES.register("dismantle", () -> new RecipeType<DismantleRecipe>() {
                @Override
                public String toString() {
                    return Dismantle.MODID + ":dismantle";
                }
            });
}
