package com.github.masyu.dismantle.menu;

import com.github.masyu.dismantle.data.DismantleEntry;
import com.github.masyu.dismantle.data.DismantleManager;
import com.github.masyu.dismantle.registries.ModBlocks;
import com.github.masyu.dismantle.registries.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

/**
 * スロット番号: 0=入力, 1-9=出力(3x3), 10-36=インベントリ, 37-45=ホットバー
 */
public class DismantleMenu extends AbstractContainerMenu {
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_START = 1;
    private static final int OUTPUT_END = 10;   // 排他的
    private static final int INV_START = 10;
    private static final int INV_END = 37;      // 排他的
    private static final int HOTBAR_START = 37;
    private static final int HOTBAR_END = 46;   // 排他的

    private final ContainerLevelAccess access;
    private final Level level;
    private boolean busy = false;

    private DismantleEntry currentEntry = null; // いま Output に並べているエントリ
    private boolean paid = false;               // Input を消費済みか
    private int generatedTotal = 0;             // 並べた直後の Output の合計個数

    private final Container input = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            DismantleMenu.this.slotsChanged(this);
        }
    };

    private final Container output = new SimpleContainer(9) {
        @Override
        public void setChanged() {
            super.setChanged();
            DismantleMenu.this.slotsChanged(this);
        }
    };

    /** クライアント側(MenuType から呼ばれる) */
    public DismantleMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    /** サーバー側(ブロックから呼ばれる) */
    public DismantleMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenus.DISMANTLE.get(), containerId);
        this.access = access;
        this.level = playerInventory.player.level();

        // 入力(左)。解体できるアイテムだけ置ける
        addSlot(new Slot(input, 0, 36, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return hasRecipeFor(stack);
            }
        });

        // 出力3x3(右)。取り出し専用
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot(output, col + row * 3, 98 + col * 18, 17 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
            }
        }

        // プレイヤーインベントリ
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // ホットバー
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

//    private boolean hasRecipeFor(ItemStack stack) {
//        int total = level.getRecipeManager()
//                .getAllRecipesFor(ModRecipes.DISMANTLING_TYPE.get()).size();
//        boolean ok = level.getRecipeManager()
//                .getRecipeFor(ModRecipes.DISMANTLING_TYPE.get(), new SimpleContainer(stack), level)
//                .isPresent();
//        System.out.println("[Cataclysm: SpellBosses] client=" + level.isClientSide + " recipes=" + total + " item=" + stack + " -> " + ok);
//        return ok;
//    }

    private boolean hasRecipeFor(ItemStack stack) {
        return DismantleManager.INSTANCE.find(stack).isPresent();
    }


    /** 出力グリッドが空で、入力にレシピがあれば、1個消費して出力に並べる */
//    private void tryDismantle() {
//        if (busy || level.isClientSide) return;
//        if (!output.isEmpty()) return;
//        if (input.getItem(0).isEmpty()) return;
//
//        Optional<DismantleRecipe> recipe = level.getRecipeManager()
//                .getRecipeFor(ModRecipes.DISMANTLING_TYPE.get(), input, level);
//        if (recipe.isEmpty()) return;
//
//        busy = true;
//        try {
//            input.removeItem(0, 1);
//            List<ItemStack> outs = recipe.get().getOutputs();
//            for (int i = 0; i < outs.size() && i < output.getContainerSize(); i++) {
//                output.setItem(i, outs.get(i));
//            }
//        } finally {
//            busy = false;
//        }
//        broadcastChanges();
//    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        refresh();
    }

    private int outputTotal() {
        int n = 0;
        for (int i = 0; i < output.getContainerSize(); i++) n += output.getItem(i).getCount();
        return n;
    }

    private void clearOutput() {
        for (int i = 0; i < output.getContainerSize(); i++) output.setItem(i, ItemStack.EMPTY);
    }

    private boolean stillMatches() {
        ItemStack in = input.getItem(0);
        return !in.isEmpty() && DismantleManager.INSTANCE.find(in)
                .map(e -> e.equals(currentEntry)).orElse(false);
    }

    private void refresh() {
        if (busy || level.isClientSide) return;
        busy = true;
        try {
            if (currentEntry != null && !paid) {
                if (outputTotal() < generatedTotal) {
                    // Output から取り出された -> Input を1個消費
                    input.removeItem(0, 1);
                    paid = true;
                } else if (!stillMatches()) {
                    // 取る前に Input が変わった -> プレビューを消す
                    clearOutput();
                    currentEntry = null;
                }
            }
            if (output.isEmpty()) {
                // 次の分を並べる(Input にまだ残っていれば)
                currentEntry = null;
                paid = false;
                Optional<DismantleEntry> found = DismantleManager.INSTANCE.find(input.getItem(0));
                if (found.isPresent()) {
                    List<ItemStack> outs = found.get().outputs();
                    for (int i = 0; i < outs.size() && i < output.getContainerSize(); i++) {
                        output.setItem(i, outs.get(i).copy());
                    }
                    currentEntry = found.get();
                    generatedTotal = outputTotal();
                }
            }
        } finally {
            busy = false;
        }
        broadcastChanges();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        access.execute((lvl, pos) -> {
            clearContainer(player, input);
            if (paid) clearContainer(player, output); // 未消費のプレビューは返さない
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.Blocks.DISMANTLE_TABLE.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < OUTPUT_END) {
            // 入力・出力 -> プレイヤー側
            if (!moveItemStackTo(stack, INV_START, HOTBAR_END, true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, INPUT_SLOT, OUTPUT_START, false)) {
            // プレイヤー側 -> 入力 が無理なら、インベントリ <-> ホットバー
            if (index < INV_END) {
                if (!moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) return ItemStack.EMPTY;
            } else if (!moveItemStackTo(stack, INV_START, INV_END, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;

        slot.onTake(player, stack);
        return original;
    }
}
