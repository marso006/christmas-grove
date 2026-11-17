package com.mental.gingerbreadgrove.registry;

import com.mental.gingerbreadgrove.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTags {

    private ModTags() {
    }

    public static final class Blocks {
        public static final TagKey<Block> SUGAR_CRYSTAL_ORES = tag("sugar_crystal_ores");
        public static final TagKey<Block> CINNAMON_ORES = tag("cinnamon_ores");

        private Blocks() {
        }

        /** {@code gingerbread_grove:<wood>_logs}: log, wood and their stripped versions. */
        public static TagKey<Block> logs(String wood) {
            return tag(wood + "_logs");
        }

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, Constants.id(name));
        }
    }

    public static final class Items {
        public static final TagKey<Item> SUGAR_CRYSTAL_ORES = tag("sugar_crystal_ores");
        public static final TagKey<Item> CINNAMON_ORES = tag("cinnamon_ores");

        private Items() {
        }

        public static TagKey<Item> logs(String wood) {
            return tag(wood + "_logs");
        }

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, Constants.id(name));
        }
    }
}
