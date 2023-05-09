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

import io.github.jamalam360.jamlib.compatibility.JamLibCompatibilityModuleHandler;
import io.github.jamalam360.jamlib.config.JamLibConfig;
import io.github.jamalam360.jamlib.log.JamLibLogger;
import io.github.jamalam360.rightclickharvest.config.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * @author Jamalam360
 */
public class RightClickHarvestModInit implements ModInitializer {

    public static final String MOD_ID = "rightclickharvest";
    public static final Direction[] CARDINAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    public static final TagKey<Block> HOE_REQUIRED =
          TagKey.of(Registries.BLOCK.getKey(), new Identifier(MOD_ID, "hoe_required"));
    public static final TagKey<Block> RADIUS_HARVEST_BLACKLIST =
          TagKey.of(Registries.BLOCK.getKey(), new Identifier(MOD_ID, "radius_harvest_blacklist"));
    public static final TagKey<Item> LOW_TIER_HOES =
          TagKey.of(Registries.ITEM.getKey(), new Identifier(MOD_ID, "low_tier_hoes"));
    public static final TagKey<Item> MID_TIER_HOES =
          TagKey.of(Registries.ITEM.getKey(), new Identifier(MOD_ID, "mid_tier_hoes"));
    public static final TagKey<Item> HIGH_TIER_HOES =
          TagKey.of(Registries.ITEM.getKey(), new Identifier(MOD_ID, "high_tier_hoes"));

    @Override
    public void onInitialize() {
        JamLibConfig.init(MOD_ID, Config.class);
        UseBlockCallback.EVENT.register(RightClickHarvestModInit::onBlockUse);
        JamLibCompatibilityModuleHandler.initialize(MOD_ID);
        JamLibLogger logger = JamLibLogger.getLogger(MOD_ID);
        logger.logInitialize();
    }

    public static ActionResult onBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        return onBlockUse(player, world, hand, hitResult, true);
    }

    private static ActionResult onBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult, boolean initialCall) {
        if (player.isSpectator() || hand == Hand.OFF_HAND) {
            return ActionResult.PASS;
        }

        BlockState state = world.getBlockState(hitResult.getBlockPos());
        Block originalBlock = state.getBlock();
        ItemStack stack = player.getStackInHand(hand);

        if (Config.useHunger) {
            if (!player.getAbilities().creativeMode && player.getHungerManager().getFoodLevel() <= 0) {
                return ActionResult.PASS;
            }
        }

        if (state.isIn(HOE_REQUIRED) && Config.requireHoe) {
            if (!stack.isIn(ItemTags.HOES)) {
                return ActionResult.PASS;
            }
        }

        if (!initialCall && state.isIn(RADIUS_HARVEST_BLACKLIST)) {
            return ActionResult.PASS;
        }

        if (state.getBlock() instanceof CocoaBlock || state.getBlock() instanceof CropBlock || state.getBlock() instanceof NetherWartBlock) {
            if (isMature(state)) {
                if (initialCall && Config.harvestInRadius && !state.isIn(RADIUS_HARVEST_BLACKLIST) && stack.isIn(ItemTags.HOES)) {
                    int radius = 0;
                    boolean circle = false;

                    if (stack.isIn(LOW_TIER_HOES)) {
                        radius = 1;
                        circle = true;
                    } else if (stack.isIn(MID_TIER_HOES)) {
                        radius = 1;
                    } else if (stack.isIn(HIGH_TIER_HOES)) {
                        radius = 2;
                        circle = true;
                    }

                    if (radius != 0) {
                        if (radius == 1 && circle) {
                            for (Direction dir : CARDINAL_DIRECTIONS) {
                                onBlockUse(player, world, hand, hitResult.withBlockPos(hitResult.getBlockPos().offset(dir)), false);
                            }
                        } else {
                            for (int x = -radius; x <= radius; x++) {
                                for (int z = -radius; z <= radius; z++) {
                                    BlockPos pos = hitResult.getBlockPos().offset(Direction.Axis.X, x).offset(Direction.Axis.Z, z);
                                    if (circle && pos.getManhattanDistance(hitResult.getBlockPos()) > radius) {
                                        continue;
                                    }
                                    onBlockUse(player, world, hand, hitResult.withBlockPos(pos), false);
                                }
                            }
                        }
                    }
                }

                if (!world.isClient) {
                    world.setBlockState(hitResult.getBlockPos(), getReplantState(state));
                    dropStacks(state, (ServerWorld) world, hitResult.getBlockPos(), player,
                          player.getStackInHand(hand)
                    );

                    if (Config.requireHoe) {
                        stack.damage(1, player, (entity) -> entity.sendToolBreakStatus(hand));
                    }

                    if (Config.useHunger) {
                        // Regular block breaking causes 0.005f exhaustion
                        player.addExhaustion(0.005f * Config.hungerLevel.modifier);
                    }
                } else {
                    player.playSound(
                          state.getBlock() instanceof NetherWartBlock ?
                          SoundEvents.ITEM_NETHER_WART_PLANT : SoundEvents.ITEM_CROP_PLANT,
                          1.0f, 1.0f
                    );
                }

                RightClickHarvestCallbacks.AFTER_HARVEST.invoker().afterHarvest(player, originalBlock);
                return ActionResult.SUCCESS;
            }
        } else if (state.getBlock() instanceof SugarCaneBlock) {
            if (hitResult.getSide() == Direction.UP && stack.getItem() == Items.SUGAR_CANE) {
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
                    player.addExhaustion(1.5f);
                }
            } else {
                player.playSound(SoundEvents.ITEM_CROP_PLANT, 1.0f, 1.0f);
            }

            RightClickHarvestCallbacks.AFTER_HARVEST.invoker().afterHarvest(player, originalBlock);
            return ActionResult.SUCCESS;
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
