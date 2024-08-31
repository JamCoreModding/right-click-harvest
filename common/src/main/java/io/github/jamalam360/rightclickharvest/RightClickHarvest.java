package io.github.jamalam360.rightclickharvest;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import io.github.jamalam360.jamlib.JamLib;
import io.github.jamalam360.jamlib.JamLibPlatform;
import io.github.jamalam360.jamlib.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

public class RightClickHarvest {

    public static final String MOD_ID = "rightclickharvest";
    public static final String MOD_NAME = "Right Click Harvest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final ConfigManager<Config> CONFIG = new ConfigManager<>(MOD_ID, Config.class);

    public static final TagKey<Block> BLACKLIST = TagKey.create(Registries.BLOCK, id("blacklist"));
    public static final TagKey<Block> HOE_NEVER_REQUIRED = TagKey.create(Registries.BLOCK, id("hoe_never_required"));
    public static final TagKey<Block> RADIUS_HARVEST_BLACKLIST = TagKey.create(Registries.BLOCK, id("radius_harvest_blacklist"));
    public static final TagKey<Item> LOW_TIER_HOES = TagKey.create(Registries.ITEM, id("low_tier_hoes"));
    public static final TagKey<Item> MID_TIER_HOES = TagKey.create(Registries.ITEM, id("mid_tier_hoes"));
    public static final TagKey<Item> HIGH_TIER_HOES = TagKey.create(Registries.ITEM, id("high_tier_hoes"));
    public static final Direction[] CARDINAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public static void init() {
        LOGGER.info("Initializing Right Click Harvest on " + JamLibPlatform.getPlatform().name());
        JamLib.checkForJarRenaming(RightClickHarvest.class);

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

    @Internal
    public static InteractionResult onBlockUse(Player player, Level level, InteractionHand hand, BlockHitResult hitResult, boolean initialCall) {
        if (player.isSpectator() || player.isCrouching() || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        BlockState state = level.getBlockState(hitResult.getBlockPos());
        ItemStack stackInHand = player.getItemInHand(hand);
        boolean hoeInUse = false;

        // Check if the block is in the blacklist
        if (state.is(BLACKLIST)) {
            return InteractionResult.PASS;
        }

        // Check for hunger, if config requires it
        if (CONFIG.get().hungerLevel != Config.HungerLevel.NONE) {
            if (!player.getAbilities().instabuild && player.getFoodData().getFoodLevel() <= 0) {
                return InteractionResult.PASS;
            }
        }

        // Check if the block requires a hoe; if so, check if a hoe is required and if the user has one.
        if (!state.is(HOE_NEVER_REQUIRED) && CONFIG.get().requireHoe) {
            if (!isHoe(stackInHand)) {
                if (isHarvestable(state) && player.level().isClientSide && !CONFIG.get().hasUserBeenWarnedForNotUsingHoe) {
                    player.displayClientMessage(
                          Component.translatable(
                                "text.rightclickharvest.use_a_hoe_warning",
                                Component.translatable("config.rightclickharvest.requireHoe").withStyle(s -> s.withColor(ChatFormatting.GREEN)),
                                Component.literal("false").withStyle(s -> s.withColor(ChatFormatting.GREEN)
                                )),
                          false
                    );
                    CONFIG.get().hasUserBeenWarnedForNotUsingHoe = true;
                    CONFIG.save();
                }

                return InteractionResult.PASS;
            } else {
                hoeInUse = true;
            }
        }

        // If we are radius harvesting and the block cannot not be, return
        if (!initialCall && state.is(RADIUS_HARVEST_BLACKLIST)) {
            return InteractionResult.PASS;
        }

        if (isReplantable(state)) {
            if (isMature(state)) {
                // Start radius harvesting
                if (initialCall && CONFIG.get().harvestInRadius && !state.is(RADIUS_HARVEST_BLACKLIST) && isHoe(stackInHand)) {
                    int radius = 0;
                    boolean circle = false;
                    hoeInUse = true;

                    if (stackInHand.is(HIGH_TIER_HOES)) {
                        radius = 2;
                        circle = true;
                    } else if (stackInHand.is(MID_TIER_HOES)) {
                        radius = 1;
                        circle = false;
                    } else if (stackInHand.is(LOW_TIER_HOES)) {
                        radius = 1;
                        circle = true;
                    }

                    if (radius == 1 && circle) {
                        for (Direction dir : CARDINAL_DIRECTIONS) {
                            onBlockUse(player, level, hand, hitResult.withPosition(hitResult.getBlockPos().relative(dir)), false);
                        }
                    } else if (radius > 0) {
                        for (int x = -radius; x <= radius; x++) {
                            for (int z = -radius; z <= radius; z++) {
                                if (x == 0 && z == 0) {
                                    continue;
                                }

                                BlockPos pos = hitResult.getBlockPos().relative(Direction.Axis.X, x).relative(Direction.Axis.Z, z);
                                if (circle && pos.distManhattan(hitResult.getBlockPos()) > radius) {
                                    continue;
                                }

                                onBlockUse(player, level, hand, hitResult.withPosition(pos), false);
                            }
                        }
                    }
                }

                return completeHarvest(level, state, hitResult.getBlockPos(), player, hand, stackInHand, hoeInUse, () -> level.setBlockAndUpdate(hitResult.getBlockPos(), getReplantState(state)));
            }
        } else if (isSugarCaneOrCactus(state)) {
            if (hitResult.getDirection() == Direction.UP && ((stackInHand.getItem() == Items.SUGAR_CANE && state.getBlock() instanceof SugarCaneBlock) || (stackInHand.getItem() == Items.CACTUS && state.getBlock() instanceof CactusBlock))) {
                return InteractionResult.PASS;
            }

            Block lookingFor = state.getBlock() instanceof SugarCaneBlock ? Blocks.SUGAR_CANE : Blocks.CACTUS;
            BlockPos bottom = hitResult.getBlockPos();
            while (level.getBlockState(bottom.below()).is(lookingFor)) {
                bottom = bottom.below();
            }

            // Only one block tall
            if (!level.getBlockState(bottom.above()).is(lookingFor)) {
                return InteractionResult.PASS;
            }

            final BlockPos breakPos = bottom.above(1);
            return completeHarvest(level, state, breakPos, player, hand, stackInHand, hoeInUse, () -> level.removeBlock(breakPos, false));
        }

        return InteractionResult.PASS;
    }

    private static InteractionResult completeHarvest(Level level, BlockState state, BlockPos pos, Player player, InteractionHand hand, ItemStack stackInHand, boolean hoeInUse, Runnable setBlockAction) {
        if (level.isClientSide) return completeHarvestClientSide(state, player);

        Block originalBlock = state.getBlock();
        // Event posts are for things like claim mods
        if (RightClickHarvestPlatform.postBreakEvent(level, pos, state, player)) {
            return InteractionResult.FAIL;
        }

        if (RightClickHarvestPlatform.postPlaceEvent(level, pos, player)) {
            return InteractionResult.FAIL;
        }

        ServerLevel world = (ServerLevel) level;
        List<ItemStack> drops = Block.getDrops(state, world, pos, null, player, player.getItemInHand(hand));
        dropStacks(drops, originalBlock, world, pos);
        setBlockAction.run();

        if (hoeInUse) {
            stackInHand.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }

        // Regular block breaking causes 0.005f exhaustion
        player.causeFoodExhaustion(0.008f * CONFIG.get().hungerLevel.modifier);
        RightClickHarvestPlatform.postAfterHarvestEvent(new HarvestContext(player, originalBlock));

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult completeHarvestClientSide(BlockState state, Player player) {
        SoundEvent soundEvent = state.getBlock() instanceof NetherWartBlock ? SoundEvents.NETHER_WART_PLANTED : SoundEvents.CROP_PLANTED;
        player.playSound(soundEvent, 1.0f, 1.0f);
        return InteractionResult.SUCCESS;
    }

    private static boolean isReplantable(BlockState state) {
        return isReplantable(state.getBlock());
    }

    private static boolean isReplantable(Block block) {
        return block instanceof CocoaBlock || block instanceof CropBlock || block instanceof NetherWartBlock;
    }

    private static boolean isSugarCaneOrCactus(BlockState state) {
        Block block = state.getBlock();
        return block instanceof SugarCaneBlock || block instanceof CactusBlock;
    }

    private static boolean isHarvestable(BlockState state) {
        if (isReplantable(state)) {
            return isMature(state);
        } else if (isSugarCaneOrCactus(state)) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isHoe(ItemStack stack) {
        return stack.is(ItemTags.HOES)
               || stack.is(LOW_TIER_HOES)
               || stack.is(MID_TIER_HOES)
               || stack.is(HIGH_TIER_HOES)
               || RightClickHarvestPlatform.isHoeAccordingToPlatform(stack);
    }

    private static boolean isMature(BlockState state) {
        Block block = state.getBlock();
        return switch (block) {
            case CocoaBlock cocoaBlock -> state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
            case CropBlock cropBlock -> cropBlock.isMaxAge(state);
            case NetherWartBlock netherWartBlock -> state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
            default -> false;
        };
    }

    private static BlockState getReplantState(BlockState state) {
        Block block = state.getBlock();
        return switch (block) {
            case CocoaBlock cocoaBlock -> state.setValue(CocoaBlock.AGE, 0);
            case CropBlock cropBlock -> cropBlock.getStateForAge(0);
            case NetherWartBlock netherWartBlock -> state.setValue(NetherWartBlock.AGE, 0);
            default -> state;
        };

    }

    private static void dropStacks(List<ItemStack> drops, Block block, ServerLevel world, BlockPos pos) {
        boolean needToReplant = isReplantable(block);
        Item replant = block.asItem();

        for (ItemStack droppedStack : drops) {
            if (needToReplant && droppedStack.is(replant)) {
                droppedStack.shrink(1);
                needToReplant = false;
            }

            Block.popResource(world, pos, droppedStack);
        }
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
