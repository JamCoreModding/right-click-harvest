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
import io.github.jamalam360.jamlib.log.JamLibLogger;
import io.github.jamalam360.rightclickharvest.config.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/**
 * @author Jamalam360
 */
public class RightClickHarvestModInit implements ModInitializer {
    public static final TagKey<Block> HOE_REQUIRED =
            TagKey.of(Registry.BLOCK_KEY, new Identifier("rightclickharvest", "hoe_required"));

    @Override
    public void onInitialize() {
        JamLibConfig.init("rightclickharvest", Config.class);

        UseBlockCallback.EVENT.register(RightClickHarvestModInit::onBlockUse);

        JamLibLogger.getLogger("rightclickharvest").logInitialize();
    }

    public static ActionResult onBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (player.isSpectator()) {
            return ActionResult.PASS;
        }

        BlockState state = world.getBlockState(hitResult.getBlockPos());

        System.out.println(state.getBlock());

        if (Config.useHunger) {
            if (player.getHungerManager().getFoodLevel() <= 0) {
                return ActionResult.PASS;
            }
        }

        if (state.isIn(HOE_REQUIRED) && Config.requireHoe) {
            if (!player.getStackInHand(hand).isIn(ConventionalItemTags.HOES)) {
                return ActionResult.PASS;
            }
        }

        if (state.getBlock() instanceof CocoaBlock || state.getBlock() instanceof CropBlock || state.getBlock() instanceof NetherWartBlock) {
            if (isMature(state)) {
                if (!world.isClient) {
                    world.setBlockState(hitResult.getBlockPos(), getReplantState(state));
                    dropStacks(state, (ServerWorld) world, hitResult.getBlockPos(), player,
                            player.getStackInHand(hand)
                    );

                    if (Config.requireHoe) {
                        player.getMainHandStack().damage(1, player, (entity) -> entity.sendToolBreakStatus(hand));
                    }

                    if (Config.useHunger && player.world.random.nextBoolean()) {
                        player.addExhaustion(2f);
                    }
                } else {
                    player.playSound(
                            state.getBlock() instanceof NetherWartBlock ?
                                    SoundEvents.ITEM_NETHER_WART_PLANT : SoundEvents.ITEM_CROP_PLANT,
                            1.0f, 1.0f
                    );
                }

                return ActionResult.SUCCESS;
            }
        } else if (state.getBlock() instanceof SugarCaneBlock) {
            if (hitResult.getSide() == Direction.UP && player.getStackInHand(hand).getItem() == Items.SUGAR_CANE) {
                return ActionResult.PASS;
            }

            int count = 1;

            BlockPos bottom = hitResult.getBlockPos().down();
            while (world.getBlockState(bottom).isOf(Blocks.SUGAR_CANE)) {
                count++;
                bottom = bottom.down();
            }

            if (count == 1 && !world.getBlockState(hitResult.getBlockPos().up()).isOf(Blocks.SUGAR_CANE)) {
                return ActionResult.PASS;
            }

            if (!world.isClient) {
                world.breakBlock(bottom.up(2), true);

                if (Config.useHunger && player.world.random.nextBoolean()) {
                    player.addExhaustion(2f);
                }
            } else {
                player.playSound(SoundEvents.ITEM_CROP_PLANT, 1.0f, 1.0f);
            }
        }

        return ActionResult.PASS;
    }

    private static boolean isMature(BlockState state) {
        if (state.getBlock() instanceof CocoaBlock) {
            return state.get(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
        } else if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.isMature(state);
        } else if (state.getBlock() instanceof NetherWartBlock) {
            return state.get(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
        }

        return false;
    }

    private static BlockState getReplantState(BlockState state) {
        if (state.getBlock() instanceof CocoaBlock) {
            return state.with(CocoaBlock.AGE, 0);
        } else if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.withAge(0);
        } else if (state.getBlock() instanceof NetherWartBlock) {
            return state.with(NetherWartBlock.AGE, 0);
        }

        return state;
    }

    private static void dropStacks(BlockState state, ServerWorld world, BlockPos pos, Entity entity,
                                   ItemStack toolStack) {
        Item replant = state.getBlock().getPickStack(world, pos, state).getItem();
        final boolean[] removedReplant = {false};

        Block.getDroppedStacks(state, world, pos, null, entity, toolStack).forEach(stack -> {
            if (!removedReplant[0] && stack.getItem() == replant) {
                stack.setCount(stack.getCount() - 1);
                removedReplant[0] = true;
            }

            Block.dropStack(world, pos, stack);
        });

        state.onStacksDropped(world, pos, toolStack, true);
    }
}
