package com.github.masyu.dismantle.block;

import com.github.masyu.dismantle.Dismantle;
import com.github.masyu.dismantle.menu.DismantleMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DismantleTableBlock extends CraftingTableBlock {
    private static final Component TITLE =
            Component.translatable("container." + Dismantle.MODID + ".dismantle");

    public DismantleTableBlock(Properties properties) {
        super(properties);
    }

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 11, 0, 16, 16, 16),   // 天板
            Block.box(2, 2, 2, 14, 11, 14),    // 台座
            Block.box(1, 0, 1, 15, 2, 15));    // 土台

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider(
                (id, inventory, player) -> new DismantleMenu(id, inventory, ContainerLevelAccess.create(level, pos)),
                TITLE);
    }
}
