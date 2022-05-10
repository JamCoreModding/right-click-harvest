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

import io.github.jamalam360.rightclickharvest.config.Config;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Method;

/**
 * @author Jamalam360
 */
@SuppressWarnings("unused")
public class RightClickHarvestGameTest implements FabricGameTest {
    @Override
    public void invokeTestMethod(TestContext context, Method method) {
        Config.requireHoe = false;
        FabricGameTest.super.invokeTestMethod(context, method);
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void testRegularCrops(TestContext context) {
        context.setBlockState(0, 2, 0, Blocks.FARMLAND);
        context.setBlockState(0, 3, 0, Blocks.WHEAT.getDefaultState().with(CropBlock.AGE, CropBlock.MAX_AGE));
        context.useBlock(new BlockPos(0, 3, 0));

        context.addInstantFinalTask(() -> {
            context.expectEntity(EntityType.ITEM);
            context.expectBlockProperty(new BlockPos(0, 3, 0), CropBlock.AGE, 0);
            context.complete();
        });
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void testRegularCropsWithHoe(TestContext context) {
        Config.requireHoe = true;
        context.setBlockState(0, 2, 0, Blocks.FARMLAND);
        context.setBlockState(0, 3, 0, Blocks.WHEAT.getDefaultState().with(CropBlock.AGE, CropBlock.MAX_AGE));

        BlockPos blockPos = context.getAbsolutePos(new BlockPos(0, 3, 0));
        BlockState blockState = context.getWorld().getBlockState(blockPos);

        PlayerEntity player = context.createMockPlayer();
        player.setStackInHand(Hand.MAIN_HAND, Items.WOODEN_HOE.getDefaultStack());

        blockState.onUse(
                context.getWorld(), player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(blockPos), Direction.NORTH, blockPos, true)
        );

        context.addInstantFinalTask(() -> {
            context.expectEntity(EntityType.ITEM);
            context.expectBlockProperty(new BlockPos(0, 3, 0), CropBlock.AGE, 0);

            if (player.getMainHandStack().getDamage() == ToolMaterials.DIAMOND.getDurability()) {
                throw new GameTestException("Expected hoe to be damaged");
            }

            context.complete();
        });
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void testRegularCropsWithNonEmptyHand(TestContext context) {
        context.setBlockState(0, 2, 0, Blocks.FARMLAND);
        context.setBlockState(0, 3, 0, Blocks.WHEAT.getDefaultState().with(CropBlock.AGE, CropBlock.MAX_AGE));

        BlockPos blockPos = context.getAbsolutePos(new BlockPos(0, 3, 0));
        BlockState blockState = context.getWorld().getBlockState(blockPos);

        PlayerEntity player = context.createMockPlayer();
        player.setStackInHand(Hand.MAIN_HAND, Items.WOODEN_HOE.getDefaultStack());

        blockState.onUse(
                context.getWorld(), player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(blockPos), Direction.NORTH, blockPos, true)
        );

        context.addInstantFinalTask(() -> {
            context.dontExpectEntity(EntityType.ITEM);
            context.expectBlockProperty(new BlockPos(0, 3, 0), CropBlock.AGE, CropBlock.MAX_AGE);
            context.complete();
        });
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void testCocoaBeans(TestContext context) {
        context.setBlockState(0, 2, 0, Blocks.JUNGLE_LOG);
        context.setBlockState(1, 2, 0, Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, CocoaBlock.MAX_AGE));
        context.useBlock(new BlockPos(1, 2, 0));

        context.addInstantFinalTask(() -> {
            context.expectEntity(EntityType.ITEM);
            context.expectBlockProperty(new BlockPos(1, 2, 0), CocoaBlock.AGE, 0);
            context.complete();
        });
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void testNetherWart(TestContext context) {
        context.setBlockState(0, 2, 0, Blocks.SOUL_SAND);
        context.setBlockState(0, 3, 0, Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, NetherWartBlock.MAX_AGE));
        context.useBlock(new BlockPos(0, 3, 0));

        context.addInstantFinalTask(() -> {
            context.expectEntity(EntityType.ITEM);
            context.expectBlockProperty(new BlockPos(0, 3, 0), NetherWartBlock.AGE, 0);
            context.complete();
        });
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void testSugarcane(TestContext context) {
        context.setBlockState(0, 2, 0, Blocks.SAND);
        context.setBlockState(0, 3, 0, Blocks.SUGAR_CANE);
        context.setBlockState(0, 4, 0, Blocks.SUGAR_CANE);
        context.useBlock(new BlockPos(0, 3, 0));

        context.addInstantFinalTask(() -> {
            context.expectEntity(EntityType.ITEM);
            context.expectBlock(Blocks.SUGAR_CANE, new BlockPos(0, 3, 0));
            context.expectBlock(Blocks.AIR, new BlockPos(0, 4, 0));
            context.complete();
        });
    }
}
