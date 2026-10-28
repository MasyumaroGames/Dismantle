package com.github.masyu.dismantle.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

/**
 * 解体1件分のデータ。
 *
 * @param id         JSONのID(data/<namespace>/dismantling/<path>.json)
 * @param input      解体対象
 * @param inputCount 消費する個数
 * @param outputs    出力グリッド(3x3 = 9マス)。インデックス = 行 * 3 + 列
 */
public record DismantleEntry(ResourceLocation id, Ingredient input, int inputCount, NonNullList<ItemStack> outputs) {
    public static final int GRID = 3;
    public static final int SIZE = GRID * GRID;

    public static DismantleEntry fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("input")) {
            throw new JsonSyntaxException("Missing 'input'");
        }
        Ingredient input = Ingredient.fromJson(json.get("input"));

        int inputCount = GsonHelper.getAsInt(json, "input_count", 1);
        if (inputCount < 1) {
            throw new JsonSyntaxException("'input_count' must be at least 1");
        }

        // key: 文字 -> 出力アイテム
        JsonObject keyJson = GsonHelper.getAsJsonObject(json, "key");
        Map<Character, ItemStack> key = new HashMap<>();
        for (Map.Entry<String, JsonElement> e : keyJson.entrySet()) {
            String k = e.getKey();
            if (k.length() != 1) {
                throw new JsonSyntaxException("Key '" + k + "' must be a single character");
            }
            if (k.equals(" ")) {
                throw new JsonSyntaxException("Key ' ' is reserved for empty slots");
            }
            JsonObject stackJson = GsonHelper.convertToJsonObject(e.getValue(), "key '" + k + "'");
            key.put(k.charAt(0), ShapedRecipe.itemStackFromJson(stackJson));
        }

        // pattern: 上の行から順に、左上基準で3x3に配置する
        JsonArray pattern = GsonHelper.getAsJsonArray(json, "pattern");
        if (pattern.isEmpty() || pattern.size() > GRID) {
            throw new JsonSyntaxException("'pattern' must have 1 to " + GRID + " rows");
        }

        NonNullList<ItemStack> outputs = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        boolean any = false;
        for (int r = 0; r < pattern.size(); r++) {
            String row = GsonHelper.convertToString(pattern.get(r), "pattern[" + r + "]");
            if (row.length() > GRID) {
                throw new JsonSyntaxException("pattern[" + r + "] is longer than " + GRID + " characters");
            }
            for (int c = 0; c < row.length(); c++) {
                char ch = row.charAt(c);
                if (ch == ' ') continue;
                ItemStack stack = key.get(ch);
                if (stack == null) {
                    throw new JsonSyntaxException("Pattern uses '" + ch + "' but it is not defined in 'key'");
                }
                outputs.set(r * GRID + c, stack.copy());
                any = true;
            }
        }
        if (!any) {
            throw new JsonSyntaxException("'pattern' has no outputs");
        }
        return new DismantleEntry(id, input, inputCount, outputs);
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id);
        input.toNetwork(buf);
        buf.writeVarInt(inputCount);
        for (ItemStack s : outputs) {
            buf.writeItem(s);
        }
    }

    public static DismantleEntry fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        Ingredient input = Ingredient.fromNetwork(buf);
        int inputCount = buf.readVarInt();
        NonNullList<ItemStack> outputs = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        for (int i = 0; i < SIZE; i++) {
            outputs.set(i, buf.readItem());
        }
        return new DismantleEntry(id, input, inputCount, outputs);
    }
}
