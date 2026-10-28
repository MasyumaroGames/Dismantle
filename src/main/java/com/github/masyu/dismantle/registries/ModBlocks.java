package com.github.masyu.dismantle.registries;


import com.github.masyu.dismantle.Dismantle;
import com.github.masyu.dismantle.block.DismantleTableBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static class Blocks {

        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Dismantle.MODID);

        public static final RegistryObject<Block> DISMANTLE_TABLE = BLOCKS.register("dismantle_table",
                () -> new DismantleTableBlock(BlockBehaviour.Properties.of()
                        .strength(5.0F, 10.0F)
                        .sound(SoundType.STONE).noOcclusion()
                )
        );
    }

    /**
     * BlockItems
     */

    public static class BlockItems {

        public static final DeferredRegister<Item> BLOCKS_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Dismantle.MODID);

        // Dismantle Table
        public static final RegistryObject<Item> DISMANTLE_TABLE = BLOCKS_ITEMS.register("dismantle_table",
                () -> new BlockItem(Blocks.DISMANTLE_TABLE.get(), new Item.Properties().stacksTo(64).rarity(Rarity.RARE)));
    }
}

