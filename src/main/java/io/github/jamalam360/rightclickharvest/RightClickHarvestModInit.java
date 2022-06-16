/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Jamalam360
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.jamalam360.rightclickharvest;

import io.github.jamalam360.jamlib.config.JamLibConfig;
import io.github.jamalam360.rightclickharvest.config.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;

import java.util.Collections;
import java.util.List;

/**
 * @author Jamalam360
 */
public class RightClickHarvestModInit implements ModInitializer {
    public static boolean canRightClickHarvest(PlayerEntity player) {
        return !Config.requireHoe || (player.getMainHandStack().isIn(ConventionalItemTags.HOES) || player.getMainHandStack().getItem() instanceof HoeItem);
    }

    public static void dropStacks(BlockState state, ServerWorld world, BlockPos pos, Entity entity, ItemStack toolStack) {
        List<ItemStack> stacks = Block.getDroppedStacks(state, world, pos, null, entity, toolStack);
        Item replant = state.getBlock().getPickStack(world, pos, state).getItem();
        boolean removedReplant = false;

        for (ItemStack stack : stacks) {
            if (!removedReplant && stack.getItem() == replant) {
                removedReplant = true;
                continue;
            }

            Block.dropStack(world, pos, stack);
        }
    }

    @Override
    public void onInitialize() {
        LogManager.getLogger("RightClickHarvest/Initializer").info("Initializing RightClickHarvest...");
        JamLibConfig.init("rightclickharvest", Config.class);
    }
}
