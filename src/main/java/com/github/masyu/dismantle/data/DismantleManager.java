package com.github.masyu.dismantle.data;

import com.github.masyu.dismantle.Dismantle;
import com.github.masyu.dismantle.registries.ModConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.*;

/**
 * data/<namespace>/dismantling/*.json を読み込む。
 * サーバーで読み込んだ内容は SyncDismantlePacket でクライアントにも送られる。
 */
public class DismantleManager extends SimpleJsonResourceReloadListener {
    public static final String FOLDER = "dismantling";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DismantleManager INSTANCE = new DismantleManager();   // 最後に置く

    private volatile Map<ResourceLocation, DismantleEntry> entries = Map.of();

    private DismantleManager() {
        super(GSON, FOLDER);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, DismantleEntry> map = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, JsonElement> file : files.entrySet()) {
            ResourceLocation id = file.getKey();
            try {
                map.put(id, DismantleEntry.fromJson(id, GsonHelper.convertToJsonObject(file.getValue(), "dismantling")));
            } catch (Exception e) {
                LOGGER.error("Failed to load dismantling entry {}: {}", id, e.getMessage());
            }
        }
        this.entries = map;
        LOGGER.info("Loaded {} dismantling entries", map.size());
    }

    /** 入力アイテムに一致する最初のエントリ(複数一致する場合の順序は不定なので、対象が被らないようにする) */
    public Optional<DismantleEntry> find(ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();

        DismantleEntry builtIn = null;
        for (DismantleEntry e : entries.values()) {
            if (!e.input().test(stack)) continue;
            if (!isBuiltInVanilla(e)) {
                return Optional.of(e);          // データパック等の追加分を優先する
            }
            if (builtIn == null) builtIn = e;
        }
        return ModConfig.enableBuiltInVanillaRecipes()
                ? Optional.ofNullable(builtIn)
                : Optional.empty();
    }

    /** このModに標準搭載の、バニラアイテム用の解体レシピか */
    private static boolean isBuiltInVanilla(DismantleEntry e) {
        ResourceLocation id = e.id();
        return Dismantle.MODID.equals(id.getNamespace()) && id.getPath().startsWith("vanilla/");
    }

    public Collection<DismantleEntry> getEntries() {
        return entries.values();
    }

    /** クライアント側で、サーバーから受け取った内容に置き換える */
    public void setEntries(Collection<DismantleEntry> list) {
        Map<ResourceLocation, DismantleEntry> map = new LinkedHashMap<>();
        for (DismantleEntry e : list) {
            map.put(e.id(), e);
        }
        this.entries = map;
    }

    public Collection<DismantleEntry> all() {
        return List.copyOf(entries.values());
    }
}
