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

package io.github.jamalam360.rightclickharvest.gametest;

import io.github.jamalam360.rightclickharvest.config.Config;
import io.github.jamalam360.rightclickharvest.gametest.TestConfigManager.HarvestInRadius;
import io.github.jamalam360.rightclickharvest.gametest.TestConfigManager.RequireHoe;
import io.github.jamalam360.rightclickharvest.gametest.TestConfigManager.UseHunger;
import java.lang.reflect.Method;
import java.util.Map;
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
import net.minecraft.util.math.BlockPos;

public class Entrypoint implements FabricGameTest {

    @Override
    public void invokeTestMethod(TestContext context, Method method) {
        Config.useHunger = false;
        Config.requireHoe = false;
        Config.harvestInRadius = false;
        TestConfigManager.apply(method);

        FabricGameTest.super.invokeTestMethod(context, method);
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void base(TestContext ctx) {
        BlockPos pos = TestHelper.wheat(ctx);
        TestHelper.interact(ctx, pos, Items.AIR.getDefaultStack());

        ctx.addInstantFinalTask(() -> {
            ctx.expectEntity(EntityType.ITEM);
            ctx.expectBlockProperty(pos, CropBlock.AGE, 0);
            ctx.complete();
        });
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void sugarcane(TestContext ctx) {
        BlockPos pos = TestHelper.sugarcane(ctx);
        TestHelper.interact(ctx, pos, Items.AIR.getDefaultStack());

        ctx.addInstantFinalTask(() -> {
            ctx.expectEntity(EntityType.ITEM);
            ctx.expectBlock(Blocks.SUGAR_CANE, new BlockPos(0, 2, 0));
            ctx.expectBlock(Blocks.AIR, new BlockPos(0, 3, 0));
            ctx.complete();
        });
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void cocoaBeans(TestContext ctx) {
        BlockPos pos = TestHelper.cocoaBeans(ctx);
        TestHelper.interact(ctx, pos, Items.AIR.getDefaultStack());

        ctx.addInstantFinalTask(() -> {
            ctx.expectEntity(EntityType.ITEM);
            ctx.expectBlockProperty(pos, CocoaBlock.AGE, 0);
            ctx.complete();
        });
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void netherWart(TestContext ctx) {
        BlockPos pos = TestHelper.netherWart(ctx);
        TestHelper.interact(ctx, pos, Items.AIR.getDefaultStack());

        ctx.addInstantFinalTask(() -> {
            ctx.expectEntity(EntityType.ITEM);
            ctx.expectBlockProperty(pos, NetherWartBlock.AGE, 0);
            ctx.complete();
        });
    }

    @UseHunger
    @GameTest(structureName = EMPTY_STRUCTURE, maxAttempts = 50)
    public void hunger(TestContext ctx) {
        BlockPos pos = TestHelper.wheat(ctx);
        PlayerEntity player = TestHelper.createMockPlayer(ctx);
        TestHelper.interact(ctx, player, pos, Items.AIR.getDefaultStack());

        ctx.addInstantFinalTask(() -> {
            ctx.expectEntity(EntityType.ITEM);
            ctx.expectBlockProperty(pos, CropBlock.AGE, 0);

            if (player.getHungerManager().getExhaustion() == 0) {
                throw new GameTestException("Expected player to have exhaustion");
            }

            ctx.complete();
        });
    }

    @UseHunger
    @GameTest(structureName = EMPTY_STRUCTURE)
    public void noHunger(TestContext ctx) {
        BlockPos pos = TestHelper.wheat(ctx);
        PlayerEntity player = TestHelper.createMockPlayer(ctx);
        player.getHungerManager().setFoodLevel(0);
        player.getHungerManager().setSaturationLevel(0);
        TestHelper.interact(ctx, player, pos, Items.AIR.getDefaultStack());

        ctx.addInstantFinalTask(() -> {
            ctx.dontExpectEntity(EntityType.ITEM);
            ctx.expectBlockProperty(pos, CropBlock.AGE, CropBlock.MAX_AGE);
            ctx.complete();
        });
    }

    @RequireHoe
    @GameTest(structureName = EMPTY_STRUCTURE)
    public void hoe(TestContext ctx) {
        BlockPos pos = TestHelper.wheat(ctx);
        PlayerEntity player = TestHelper.createMockPlayer(ctx);
        TestHelper.interact(ctx, player, pos, Items.WOODEN_HOE.getDefaultStack());

        ctx.addInstantFinalTask(() -> {
            ctx.expectEntity(EntityType.ITEM);
            ctx.expectBlockProperty(pos, CropBlock.AGE, 0);

            if (player.getStackInHand(Hand.MAIN_HAND).getDamage() == ToolMaterials.WOOD.getDurability()) {
                throw new GameTestException("Expected hoe to be damaged");
            }

            ctx.complete();
        });
    }

    @RequireHoe
    @GameTest(structureName = EMPTY_STRUCTURE)
    public void noHoe(TestContext ctx) {
        BlockPos pos = TestHelper.wheat(ctx);
        TestHelper.interact(ctx, pos, Items.AIR.getDefaultStack());

        ctx.addInstantFinalTask(() -> {
            ctx.dontExpectEntity(EntityType.ITEM);
            ctx.expectBlockProperty(pos, CropBlock.AGE, CropBlock.MAX_AGE);
            ctx.complete();
        });
    }

    @UseHunger
    @RequireHoe
    @GameTest(structureName = EMPTY_STRUCTURE, maxAttempts = 50)
    public void hoeAndHunger(TestContext ctx) {
        BlockPos pos = TestHelper.wheat(ctx);
        PlayerEntity player = TestHelper.createMockPlayer(ctx);
        TestHelper.interact(ctx, player, pos, Items.WOODEN_HOE.getDefaultStack());

        ctx.addInstantFinalTask(() -> {
            ctx.expectEntity(EntityType.ITEM);
            ctx.expectBlockProperty(pos, CropBlock.AGE, 0);

            if (player.getStackInHand(Hand.MAIN_HAND).getDamage() == ToolMaterials.WOOD.getDurability()) {
                throw new GameTestException("Expected hoe to be damaged");
            }

            if (player.getHungerManager().getExhaustion() == 0) {
                throw new GameTestException("Expected player to have exhaustion");
            }

            ctx.complete();
        });
    }

    @RequireHoe
    @GameTest(structureName = EMPTY_STRUCTURE, maxAttempts = 100)
    public void fortuneHoe(TestContext ctx) {
        BlockPos pos = TestHelper.wheat(ctx);
        PlayerEntity player = TestHelper.createMockPlayer(ctx);
        ItemStack hoe = Items.WOODEN_HOE.getDefaultStack();
        EnchantmentHelper.set(Map.of(Enchantments.FORTUNE, 3), hoe);
        TestHelper.interact(ctx, player, pos, hoe);

        ctx.addInstantFinalTask(() -> {
            ctx.expectItemsAt(Items.WHEAT_SEEDS, new BlockPos(0, 4, 0), 4, 4);
            ctx.expectBlockProperty(pos, CropBlock.AGE, 0);

            if (player.getStackInHand(Hand.MAIN_HAND).getDamage() == ToolMaterials.WOOD.getDurability()) {
                throw new GameTestException("Expected hoe to be damaged");
            }

            ctx.complete();
        });
    }

    @HarvestInRadius
    @GameTest(structureName = EMPTY_STRUCTURE)
    public void radius(TestContext ctx) {
        BlockPos[] interactionPositions = TestHelper.radius(ctx);
        PlayerEntity player = TestHelper.createMockPlayer(ctx);
        ItemStack hoe = Items.NETHERITE_HOE.getDefaultStack();
        TestHelper.interact(ctx, player, new BlockPos(1, 3, 1), hoe);

        ctx.addInstantFinalTask(() -> {
            ctx.expectEntity(EntityType.ITEM);

            for (BlockPos pos : interactionPositions) {
                ctx.expectBlockProperty(pos, CropBlock.AGE, 0);
            }

            if (player.getStackInHand(Hand.MAIN_HAND).getDamage() == ToolMaterials.NETHERITE.getDurability()) {
                throw new GameTestException("Expected hoe to be damaged");
            }

            ctx.complete();
        });
    }

    @HarvestInRadius
    @UseHunger
    @GameTest(structureName = EMPTY_STRUCTURE)
    public void noHungerRadius(TestContext ctx) {
        BlockPos[] interactionPositions = TestHelper.radius(ctx);
        PlayerEntity player = TestHelper.createMockPlayer(ctx);
        player.getHungerManager().setFoodLevel(0);
        player.getHungerManager().setSaturationLevel(0);
        ItemStack hoe = Items.NETHERITE_HOE.getDefaultStack();
        TestHelper.interact(ctx, player, new BlockPos(1, 3, 1), hoe);

        ctx.addInstantFinalTask(() -> {
            ctx.dontExpectEntity(EntityType.ITEM);

            for (BlockPos pos : interactionPositions) {
                ctx.expectBlockProperty(pos, CropBlock.AGE, CropBlock.MAX_AGE);
            }

            if (player.getStackInHand(Hand.MAIN_HAND).isDamaged()) {
                throw new GameTestException("Expected hoe to not be damaged");
            }

            ctx.complete();
        });
    }

    @HarvestInRadius
    @RequireHoe
    @UseHunger
    @GameTest(structureName = EMPTY_STRUCTURE)
    public void noHungerHoeAndRadius(TestContext ctx) {
        noHungerRadius(ctx);
    }
}
