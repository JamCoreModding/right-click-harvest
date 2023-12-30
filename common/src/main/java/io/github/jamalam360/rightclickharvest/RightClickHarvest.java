package io.github.jamalam360.rightclickharvest;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import io.github.jamalam360.jamlib.JamLibPlatform;
import io.github.jamalam360.jamlib.config.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RightClickHarvest {
	public static final String MOD_ID = "rightclickharvest";
	public static final String MOD_NAME = "Right Click Harvest";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	public static final ConfigManager<Config> CONFIG = new ConfigManager<>(MOD_ID, Config.class);
	public static final TagKey<Block> HOE_NEVER_REQUIRED = TagKey.create(Registries.BLOCK,
			id("hoe_never_required"));
	public static final TagKey<Block> RADIUS_HARVEST_BLACKLIST = TagKey.create(Registries.BLOCK,
			id("radius_harvest_blacklist"));
	public static final TagKey<Item> LOW_TIER_HOES = TagKey.create(Registries.ITEM, id("low_tier_hoes"));
	public static final TagKey<Item> MID_TIER_HOES = TagKey.create(Registries.ITEM, id("mid_tier_hoes"));
	public static final TagKey<Item> HIGH_TIER_HOES = TagKey.create(Registries.ITEM, id("high_tier_hoes"));

	public static void init() {
		LOGGER.info("Initializing Right Click Harvest on " + JamLibPlatform.getPlatform().name());

		InteractionEvent.RIGHT_CLICK_BLOCK.register(((player, hand, pos, face) -> {
			InteractionResult res = RightClickHarvest.onBlockUse(player, player.level(), hand, new BlockHitResult(player.position(), face, pos, false), true);

			return switch (res) {
				case SUCCESS -> EventResult.interruptTrue();
				case PASS -> EventResult.pass();
				case FAIL -> EventResult.interruptFalse();
				default -> throw new IllegalStateException("Unexpected value: " + res);
			};
		}));
	}

	private static InteractionResult onBlockUse(Player player, Level world, InteractionHand hand, BlockHitResult hitResult, boolean initialCall) {
		if (player.isSpectator() || player.isCrouching() || hand == InteractionHand.OFF_HAND) {
			return InteractionResult.PASS;
		}

		BlockState state = world.getBlockState(hitResult.getBlockPos());
		Block originalBlock = state.getBlock();
		ItemStack stack = player.getItemInHand(hand);

		if (CONFIG.get().hungerLevel != Config.HungerLevel.NONE) {
			if (!player.getAbilities().instabuild && player.getFoodData().getFoodLevel() <= 0) {
				return InteractionResult.PASS;
			}
		}

		if (!state.is(HOE_NEVER_REQUIRED) && CONFIG.get().requireHoe) {
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
				if (initialCall && CONFIG.get().harvestInRadius && !state.is(RADIUS_HARVEST_BLACKLIST)
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
							for (Direction dir : new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH,
									Direction.WEST}) {
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
					// The following event posts are for things like claim mods
					if (RightClickHarvestPlatform.postBreakEvent(world, hitResult.getBlockPos(), state, player)) {
						return InteractionResult.FAIL;
					}

					if (RightClickHarvestPlatform.postPlaceEvent(world, hitResult.getBlockPos(), player)) {
						return InteractionResult.FAIL;
					}

					world.setBlockAndUpdate(hitResult.getBlockPos(), getReplantState(state));
					dropStacks(state, (ServerLevel) world, hitResult.getBlockPos(), player,
							player.getItemInHand(hand), true);

					if (CONFIG.get().requireHoe) {
						stack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(hand));
					}

					// Regular block breaking causes 0.005f exhaustion
					player.causeFoodExhaustion(0.008f * CONFIG.get().hungerLevel.modifier);
				} else {
					player.playSound(
							state.getBlock() instanceof NetherWartBlock ? SoundEvents.NETHER_WART_PLANTED
									: SoundEvents.CROP_PLANTED,
							1.0f, 1.0f);
				}

				RightClickHarvestPlatform.postAfterHarvestEvent(new HarvestContext(player, originalBlock));
				return InteractionResult.SUCCESS;
			}
		} else if (state.getBlock() instanceof SugarCaneBlock || state.getBlock() instanceof CactusBlock) {
			if (hitResult.getDirection() == Direction.UP && ((stack.getItem() == Items.SUGAR_CANE && state
					.getBlock() instanceof SugarCaneBlock) || (stack.getItem() == Items.CACTUS && state
					.getBlock() instanceof CactusBlock))) {
				return InteractionResult.PASS;
			}

			Block lookingFor = state.getBlock() instanceof SugarCaneBlock ? Blocks.SUGAR_CANE : Blocks.CACTUS;
			BlockPos bottom = hitResult.getBlockPos().below();
			while (world.getBlockState(bottom).is(lookingFor)) {
				bottom = bottom.below();
			}

			if (bottom.equals(hitResult.getBlockPos().below())) {
				return InteractionResult.PASS;
			}

			if (!world.isClientSide) {
				// The following event posts are for things like claim mods
				if (RightClickHarvestPlatform.postBreakEvent(world, hitResult.getBlockPos(), state, player)) {
					return InteractionResult.FAIL;
				}

				if (RightClickHarvestPlatform.postPlaceEvent(world, hitResult.getBlockPos(), player)) {
					return InteractionResult.FAIL;
				}

				dropStacks(state, (ServerLevel) world, bottom.above(2), player,
						player.getItemInHand(hand), false);
				world.removeBlock(bottom.above(2), false);

				if (CONFIG.get().requireHoe) {
					stack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(hand));
				}

				// Regular block breaking causes 0.005f exhaustion
				player.causeFoodExhaustion(0.008f * CONFIG.get().hungerLevel.modifier);
			} else {
				player.playSound(SoundEvents.CROP_PLANTED, 1.0f, 1.0f);
			}

			RightClickHarvestPlatform.postAfterHarvestEvent(new HarvestContext(player, originalBlock));
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
	                               ItemStack toolStack, boolean removeReplant) {
		Item replant = state.getBlock().getCloneItemStack(world, pos, state).getItem();
		final boolean[] removedReplant = {!removeReplant};

		Block.getDrops(state, world, pos, null, entity, toolStack).forEach(stack -> {
			if (!removedReplant[0] && stack.getItem() == replant) {
				stack.setCount(stack.getCount() - 1);
				removedReplant[0] = true;
			}

			Block.popResource(world, pos, stack);
		});

		state.spawnAfterBreak(world, pos, toolStack, true);
	}

	private static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
