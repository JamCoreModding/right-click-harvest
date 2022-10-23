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
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
import java.util.Map;

/**
 * @author Jamalam360
 */
@SuppressWarnings("unused")
public class RightClickHarvestGameTest implements FabricGameTest {
    private int fortuneInvocations = 0;

    @Override
    public void invokeTestMethod(TestContext context, Method method) {
        Config.requireHoe = false;
        Config.harvestInRadius = false;
        FabricGameTest.super.invokeTestMethod(context, method);
    }

    public void interactWithBlock(TestContext context, BlockPos pos) {
        interactWithBlock(context, pos, Items.AIR.getDefaultStack());
    }

    public PlayerEntity interactWithBlock(TestContext context, BlockPos pos, ItemStack stack) {
        pos = context.getAbsolutePos(pos);
        PlayerEntity player = context.createMockPlayer();
        player.setStackInHand(Hand.MAIN_HAND, stack);
        BlockHitResult hitResult = new BlockHitResult(Vec3d.ofCenter(pos), Direction.NORTH, pos, false);
        RightClickHarvestModInit.onBlockUse(player, player.world, Hand.MAIN_HAND, hitResult);
        return player;
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void testRegularCrops(TestContext context) {
        context.setBlockState(0, 2, 0, Blocks.FARMLAND);
        context.setBlockState(0, 3, 0, Blocks.WHEAT.getDefaultState().with(CropBlock.AGE, CropBlock.MAX_AGE));
        interactWithBlock(context, new BlockPos(0, 3, 0));

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

        PlayerEntity player = interactWithBlock(context, new BlockPos(0, 3, 0), Items.WOODEN_HOE.getDefaultStack());

        context.addInstantFinalTask(() -> {
            context.expectEntity(EntityType.ITEM);
            context.expectBlockProperty(new BlockPos(0, 3, 0), CropBlock.AGE, 0);

            if (player.getMainHandStack().getDamage() == ToolMaterials.WOOD.getDurability()) {
                throw new GameTestException("Expected hoe to be damaged");
            }

            context.complete();
        });
    }

    /*
     * This test runs itself up to 20 times.
     * It has a 0.097366173% chance of failing, even if the functionality is correct.
     * Mald.
     * Seethe.
     * Cope.
     * */
    @GameTest(structureName = EMPTY_STRUCTURE)
    public void testRegularCropsWithFortuneHoe(TestContext context) {
        Config.requireHoe = true;
        fortuneInvocations++;
        context.killAllEntities();
        context.setBlockState(0, 2, 0, Blocks.FARMLAND);
        context.setBlockState(0, 3, 0, Blocks.WHEAT.getDefaultState().with(CropBlock.AGE, CropBlock.MAX_AGE));

        ItemStack hoe = Items.DIAMOND_HOE.getDefaultStack();
        EnchantmentHelper.set(Map.of(Enchantments.FORTUNE, 3), hoe);
        PlayerEntity player = interactWithBlock(context, new BlockPos(0, 3, 0), hoe);

        try {
            // 4+ seeds are only dropped if the hoe is enchanted with fortune. With fortune 3, there is a 29.3% chance of 4 seeds being dropped.
            context.expectItemsAt(Items.WHEAT_SEEDS, new BlockPos(0, 4, 0), 4, 4);
        } catch (GameTestException e) {
            if (fortuneInvocations > 20) {
                throw new GameTestException("Fortune test failed after 20 invocations");
            } else {
                testRegularCropsWithFortuneHoe(context);
                return;
            }
        }

        context.expectBlockProperty(new BlockPos(0, 3, 0), CropBlock.AGE, 0);

        if (player.getMainHandStack().getDamage() == ToolMaterials.DIAMOND.getDurability()) {
            throw new GameTestException("Expected hoe to be damaged");
        }

        context.complete();
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void testRegularCropsWithHoeInRadius(TestContext context) {
        Config.requireHoe = true;
        runHarvestInRadiusTest(context);
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void testRegularCropsWithHoeInRadiusWithNonRequiredHoe(TestContext context) {
        runHarvestInRadiusTest(context);
    }

    private void runHarvestInRadiusTest(TestContext context) {
        Config.harvestInRadius = true;

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    context.setBlockState(x, 2 + y, z, y == 0 ? Blocks.FARMLAND.getDefaultState() : Blocks.WHEAT.getDefaultState().with(CropBlock.AGE, CropBlock.MAX_AGE));
                }
            }
        }

        PlayerEntity player = interactWithBlock(context, new BlockPos(1, 3, 1), Items.DIAMOND_HOE.getDefaultStack());

        context.addInstantFinalTask(() -> {
            context.expectEntity(EntityType.ITEM);

            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    context.expectBlockProperty(new BlockPos(x, 3, z), CropBlock.AGE, 0);
                }
            }

            if (player.getMainHandStack().getDamage() == ToolMaterials.DIAMOND.getDurability()) {
                throw new GameTestException("Expected hoe to be damaged");
            }

            context.complete();
        });
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void testCocoaBeans(TestContext context) {
        context.setBlockState(0, 2, 0, Blocks.JUNGLE_LOG);
        context.setBlockState(1, 2, 0, Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, CocoaBlock.MAX_AGE));
        interactWithBlock(context, new BlockPos(1, 2, 0));

        context.addInstantFinalTask(() -> {
            context.expectEntity(EntityType.ITEM);
            context.expectBlockProperty(new BlockPos(1, 2, 0), CocoaBlock.AGE, 0);
            context.complete();
        });
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void testNetherWart(TestContext context) {
        context.setBlockState(0, 2, 0, Blocks.SOUL_SAND);
        context.setBlockState(0, 3, 0, Blocks.NETHER_WART.getDefaultState().with(
                NetherWartBlock.AGE,
                NetherWartBlock.MAX_AGE
        ));
        interactWithBlock(context, new BlockPos(0, 3, 0));

        context.addInstantFinalTask(() -> {
            context.expectEntity(EntityType.ITEM);
            context.expectBlockProperty(new BlockPos(0, 3, 0), NetherWartBlock.AGE, 0);
            context.complete();
        });
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void testSugarcane(TestContext context) {
        context.setBlockState(0, 1, 0, Blocks.SAND);
        context.setBlockState(0, 2, 0, Blocks.SUGAR_CANE);
        context.setBlockState(0, 3, 0, Blocks.SUGAR_CANE);
        interactWithBlock(context, new BlockPos(0, 2, 0));

        context.addInstantFinalTask(() -> {
            context.expectEntity(EntityType.ITEM);
            context.expectBlock(Blocks.SUGAR_CANE, new BlockPos(0, 2, 0));
            context.expectBlock(Blocks.AIR, new BlockPos(0, 3, 0));
            context.complete();
        });
    }
}
