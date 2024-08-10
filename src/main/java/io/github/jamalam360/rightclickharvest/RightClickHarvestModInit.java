/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Jamalam360
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

import com.mojang.logging.LogUtils;
import io.github.jamalam360.rightclickharvest.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

/**
 * @author Jamalam360
 */
@Mod(RightClickHarvestModInit.MOD_ID)
public class RightClickHarvestModInit {

	public static final String MOD_ID = "rightclickharvest";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final Pair<Config, ForgeConfigSpec> CONFIG = new ForgeConfigSpec.Builder()
			.configure(Config::new);

	public static final Direction[] CARDINAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST,
			Direction.SOUTH, Direction.WEST};
	@Deprecated(forRemoval = true)
	public static final TagKey<Block> HOE_REQUIRED = BlockTags.create(
			new ResourceLocation(MOD_ID, "hoe_required"));
	public static final TagKey<Block> HOE_NEVER_REQUIRED = BlockTags.create(
			new ResourceLocation(MOD_ID, "hoe_never_required"));
	public static final TagKey<Block> RADIUS_HARVEST_BLACKLIST = BlockTags.create(
			new ResourceLocation(MOD_ID, "radius_harvest_blacklist"));
	public static final TagKey<Item> LOW_TIER_HOES = ItemTags.create(
			new ResourceLocation(MOD_ID, "low_tier_hoes"));
	public static final TagKey<Item> MID_TIER_HOES = ItemTags.create(
			new ResourceLocation(MOD_ID, "mid_tier_hoes"));
	public static final TagKey<Item> HIGH_TIER_HOES = ItemTags.create(
			new ResourceLocation(MOD_ID, "high_tier_hoes"));

	public RightClickHarvestModInit() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(Type.COMMON, CONFIG.getRight(), MOD_ID + ".toml");
		modEventBus.addListener(this::onCommonSetup);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void onCommonSetup(final FMLCommonSetupEvent event) {
		// MinecraftForge.EVENT_BUS.<PlayerInteractEvent.RightClickBlock>addListener(this::onBlockUse);
		LOGGER.info("Initialized");
	}

	@SubscribeEvent
	public void onBlockUse(PlayerInteractEvent.RightClickBlock event) {
		InteractionResult res = onBlockUse(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec(),
				true);

		if (res != InteractionResult.PASS) {
			event.setCanceled(true);
			event.setCancellationResult(res);
		}
	}

	private InteractionResult onBlockUse(Player player, Level world, InteractionHand hand,
	                                     net.minecraft.world.phys.BlockHitResult hitResult,
	                                     boolean initialCall) {
		if (player.isSpectator() || hand == InteractionHand.OFF_HAND) {
			return InteractionResult.PASS;
		}

		BlockState state = world.getBlockState(hitResult.getBlockPos());
		Block originalBlock = state.getBlock();
		ItemStack stack = player.getItemInHand(hand);

		if (CONFIG.getLeft().hungerLevel.get() != Config.HungerLevel.NONE) {
			if (!player.getAbilities().instabuild && player.getFoodData().getFoodLevel() <= 0) {
				return InteractionResult.PASS;
			}
		}

		if (!state.is(HOE_NEVER_REQUIRED) && CONFIG.getLeft().requireHoe.get()) {
			if (!stack.is(ItemTags.HOES)) {
				return InteractionResult.PASS;
			}
		}

		if (!initialCall && state.is(RADIUS_HARVEST_BLACKLIST)) {
			return InteractionResult.PASS;
		}

		if (state.getBlock() instanceof CocoaBlock || state.getBlock() instanceof CropBlock
				|| state.getBlock() instanceof NetherWartBlock) {
			if (isMature(state)) {
				if (initialCall && CONFIG.getLeft().harvestInRadius.get() && !state.is(RADIUS_HARVEST_BLACKLIST)
						&& stack.is(ItemTags.HOES)) {
					int radius = 0;
					boolean circle = false;

					if (stack.is(LOW_TIER_HOES)) {
						radius = 1;
						circle = true;
					} else if (stack.is(MID_TIER_HOES)) {
						radius = 1;
					} else if (stack.is(HIGH_TIER_HOES)) {
						radius = 2;
						circle = true;
					}

					if (radius != 0) {
						if (radius == 1 && circle) {
							for (Direction dir : CARDINAL_DIRECTIONS) {
								onBlockUse(player, world, hand,
										hitResult.withPosition(hitResult.getBlockPos().relative(dir)), false);
							}
						} else {
							for (int x = -radius; x <= radius; x++) {
								for (int z = -radius; z <= radius; z++) {
									BlockPos pos = hitResult.getBlockPos().relative(Direction.Axis.X, x)
											.relative(Direction.Axis.Z, z);
									if (circle && pos.distManhattan(hitResult.getBlockPos()) > radius) {
										continue;
									}
									onBlockUse(player, world, hand, hitResult.withPosition(pos), false);
								}
							}
						}
					}
				}

				if (!world.isClientSide) {
					BlockEvent.BreakEvent breakEv = new BlockEvent.BreakEvent(world, hitResult.getBlockPos(), state, player);
					if (MinecraftForge.EVENT_BUS.post(breakEv)) return InteractionResult.FAIL;
					BlockState replantState = getReplantState(state);
					BlockEvent.EntityPlaceEvent placeEv = new BlockEvent.EntityPlaceEvent(
							BlockSnapshot.create(world.dimension(), world, hitResult.getBlockPos()),
							world.getBlockState(hitResult.getBlockPos().below()),
							player
					);
					if (MinecraftForge.EVENT_BUS.post(placeEv)) return InteractionResult.FAIL;
					world.setBlockAndUpdate(hitResult.getBlockPos(), replantState);
					dropStacks(state, (ServerLevel) world, hitResult.getBlockPos(), player,
							player.getItemInHand(hand));

					if (CONFIG.getLeft().requireHoe.get()) {
						stack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(hand));
					}

					// Regular block breaking causes 0.005f exhaustion
					player.causeFoodExhaustion(0.008f * CONFIG.getLeft().hungerLevel.get().modifier);
				} else {
					player.playSound(
							state.getBlock() instanceof NetherWartBlock ? SoundEvents.NETHER_WART_PLANTED
									: SoundEvents.CROP_PLANTED,
							1.0f, 1.0f);
				}

				RightClickHarvestCallbacks.AfterHarvest.post(player, originalBlock);
				return InteractionResult.SUCCESS;
			}
		} else if (state.getBlock() instanceof SugarCaneBlock) {
			if (hitResult.getDirection() == Direction.UP && stack.getItem() == Items.SUGAR_CANE) {
				return InteractionResult.PASS;
			}

			int count = 1;

			BlockPos bottom = hitResult.getBlockPos().below();
			while (world.getBlockState(bottom).is(Blocks.SUGAR_CANE)) {
				count++;
				bottom = bottom.below();
			}

			if (count == 1 && !world.getBlockState(hitResult.getBlockPos().above()).is(Blocks.SUGAR_CANE)) {
				return InteractionResult.PASS;
			}

			if (!world.isClientSide) {
				world.destroyBlock(bottom.above(2), true);

				// Regular block breaking causes 0.005f exhaustion
				player.causeFoodExhaustion(0.008f * CONFIG.getLeft().hungerLevel.get().modifier);
			} else {
				player.playSound(SoundEvents.CROP_PLANTED, 1.0f, 1.0f);
			}

			RightClickHarvestCallbacks.AfterHarvest.post(player, originalBlock);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	private static boolean isMature(BlockState state) {
		if (state.getBlock() instanceof CocoaBlock) {
			return state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
		} else if (state.getBlock() instanceof CropBlock cropBlock) {
			return cropBlock.isMaxAge(state);
		} else if (state.getBlock() instanceof NetherWartBlock) {
			return state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
		}

		return false;
	}

	private static BlockState getReplantState(BlockState state) {
		if (state.getBlock() instanceof CocoaBlock) {
			return state.setValue(CocoaBlock.AGE, 0);
		} else if (state.getBlock() instanceof CropBlock cropBlock) {
			return cropBlock.getStateForAge(0);
		} else if (state.getBlock() instanceof NetherWartBlock) {
			return state.setValue(NetherWartBlock.AGE, 0);
		}

		return state;
	}

	private static void dropStacks(BlockState state, ServerLevel world, BlockPos pos, Entity entity,
	                               ItemStack toolStack) {
		Item replant = state.getBlock().getCloneItemStack(world, pos, state).getItem();
		final boolean[] removedReplant = {false};

		Block.getDrops(state, world, pos, null, entity, toolStack).forEach(stack -> {
			if (!removedReplant[0] && stack.getItem() == replant) {
				stack.setCount(stack.getCount() - 1);
				removedReplant[0] = true;
			}

			Block.popResource(world, pos, stack);
		});

		state.spawnAfterBreak(world, pos, toolStack, true);
	}
}
