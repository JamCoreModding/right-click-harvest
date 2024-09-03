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
            InteractionResult res = RightClickHarvest.onBlockUse(player, hand, new BlockHitResult(player.position(), face, pos, false));

            return switch (res) {
                case SUCCESS -> EventResult.interruptTrue();
                case PASS -> EventResult.pass();
                case FAIL -> EventResult.interruptFalse();
                default -> throw new IllegalStateException("Unexpected value: " + res);
            };
        }));
    }

    @Internal
    public static InteractionResult onBlockUse(Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isSpectator() || player.isCrouching() || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        return new Harvester(player, hitResult).harvest();
    }

    static class Harvester {
        private final Player player;
        private final BlockHitResult hitResult;
        private final Level level;
        private final ItemStack mainHandItem;
        private final BlockState state;
        private final Block block;

        public Harvester(Player player, BlockHitResult hitResult) {
            this.player = player;
            this.hitResult = hitResult;

            this.level = player.level();
            this.mainHandItem = player.getMainHandItem();
            this.state = level.getBlockState(hitResult.getBlockPos());
            this.block = state.getBlock();
        }

        public InteractionResult harvest() {
            if (isHoeRequiredWithWarning()) return InteractionResult.PASS;

            if (cannotHarvest()) return InteractionResult.PASS;

            if (canRadiusHarvest(player, state)) attemptRadiusHarvesting(player, hitResult);

            return maybeBlockHarvest(player, hitResult, state);
        }

        private boolean isHoeRequiredWithWarning() {
            // Check if the block requires a hoe; if so, check if a hoe is required and if the user has one.
            var required = !state.is(HOE_NEVER_REQUIRED) && CONFIG.get().requireHoe && !isHoeInHand() && isHarvestable();
            if (required) warnOnceForNotUsingHoe();
            return required;
        }

        private void warnOnceForNotUsingHoe() {
            if (CONFIG.get().hasUserBeenWarnedForNotUsingHoe || !level.isClientSide) return;

            var translatable = Component.translatable(
                    "text.rightclickharvest.use_a_hoe_warning",
                    Component.translatable("config.rightclickharvest.requireHoe").withStyle(s -> s.withColor(ChatFormatting.GREEN)),
                    Component.literal("false").withStyle(s -> s.withColor(ChatFormatting.GREEN)
                    ));
            player.displayClientMessage(translatable, false);

            CONFIG.get().hasUserBeenWarnedForNotUsingHoe = true;
            CONFIG.save();
        }

        private boolean isHoeInHand() {
            return mainHandItem.is(ItemTags.HOES)
                    || mainHandItem.is(LOW_TIER_HOES)
                    || mainHandItem.is(MID_TIER_HOES)
                    || mainHandItem.is(HIGH_TIER_HOES)
                    || RightClickHarvestPlatform.isHoeAccordingToPlatform(mainHandItem);
        }

        private boolean isHarvestable() {
            return isReplantableAndMature() || isSugarCaneOrCactus();
        }

        private boolean isReplantable() {
            return block instanceof CocoaBlock || block instanceof CropBlock || block instanceof NetherWartBlock;
        }

        private boolean isSugarCaneOrCactus() {
            return block instanceof SugarCaneBlock || block instanceof CactusBlock;
        }

        private boolean isReplantableAndMature() {
            return switch (block) {
                case CocoaBlock cocoaBlock -> state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
                case CropBlock cropBlock -> cropBlock.isMaxAge(state);
                case NetherWartBlock netherWartBlock -> state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
                default -> false;
            };
        }

        private boolean cannotHarvest() {
            return state.is(BLACKLIST) || isExhausted();
        }

        // Check for hunger, if config requires it
        private boolean isExhausted() {
            if (player.hasInfiniteMaterials()) return false;
            if (CONFIG.get().hungerLevel != Config.HungerLevel.NONE) return false;
            return player.getFoodData().getFoodLevel() <= 0;
        }

        // canRadiusHarvest(player, state)
        // attemptRadiusHarvesting(player, hitResult)
        // maybeBlockHarvest(player, hitResult, state)
    }

    private static BlockState getBlockState(Player player, BlockHitResult hitResult) {
        Level level = player.level();
        return level.getBlockState(hitResult.getBlockPos());
    }

    private static boolean canRadiusHarvest(Player player, BlockState state) {
        return CONFIG.get().harvestInRadius && !state.is(RADIUS_HARVEST_BLACKLIST) && isHoeInHand(player) && isReplantableAndMature(state);
    }

    private static boolean cannotRadiusHarvest(Player player, BlockState state) {
        return state.is(RADIUS_HARVEST_BLACKLIST) && cannotHarvest(player, state);
    }

    private static boolean cannotHarvest(Player player, BlockState state) {
        return state.is(BLACKLIST) || isExhausted(player);
    }

    private static void maybeRadiusHarvest(Player player, BlockHitResult hitResult) {
        BlockState state = getBlockState(player, hitResult);
        if (cannotRadiusHarvest(player, state)) return;
        maybeBlockHarvest(player, hitResult, state);
    }

    private static InteractionResult maybeBlockHarvest(Player player, BlockHitResult hitResult, BlockState state) {
        if (isReplantableAndMature(state)) return completeHarvest(state, player, hitResult.getBlockPos());
        if (isSugarCaneOrCactus(state)) return harvestSugarCaneOrCactus(player, hitResult, state);

        return InteractionResult.PASS;
    }

    private static void attemptRadiusHarvesting(Player player, BlockHitResult hitResult) {
        int radius = 0;
        boolean circle = false;

        var hoeInHand = player.getMainHandItem();
        if (hoeInHand.is(HIGH_TIER_HOES)) {
            radius = 2;
            circle = true;
        } else if (hoeInHand.is(MID_TIER_HOES)) {
            radius = 1;
            circle = false;
        } else if (hoeInHand.is(LOW_TIER_HOES)) {
            radius = 1;
            circle = true;
        }

        if (radius == 1 && circle) {
            for (Direction dir : CARDINAL_DIRECTIONS) {
                maybeRadiusHarvest(player, hitResult.withPosition(hitResult.getBlockPos().relative(dir)));
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

                    maybeRadiusHarvest(player, hitResult.withPosition(pos));
                }
            }
        }
    }

    private static InteractionResult harvestSugarCaneOrCactus(Player player, BlockHitResult hitResult, BlockState state) {
        ItemStack stackInHand = player.getMainHandItem();
        if (hitResult.getDirection() == Direction.UP && ((stackInHand.getItem() == Items.SUGAR_CANE && state.getBlock() instanceof SugarCaneBlock) || (stackInHand.getItem() == Items.CACTUS && state.getBlock() instanceof CactusBlock))) {
            return InteractionResult.PASS;
        }

        Block lookingFor = state.getBlock() instanceof SugarCaneBlock ? Blocks.SUGAR_CANE : Blocks.CACTUS;
        BlockPos bottom = hitResult.getBlockPos();
        Level level = player.level();
        while (level.getBlockState(bottom.below()).is(lookingFor)) {
            bottom = bottom.below();
        }

        // Only one block tall
        if (!level.getBlockState(bottom.above()).is(lookingFor)) {
            return InteractionResult.PASS;
        }

        final BlockPos breakPos = bottom.above(1);
        return completeHarvest(state, player, breakPos);
    }

    private static InteractionResult completeHarvest(BlockState state, Player player, BlockPos pos) {
        Level level = player.level();

        if (level.isClientSide) return playSoundClientSide(state, player);

        // ==== Server Side only below ====

        // Event posts are for things like claim mods
        if (RightClickHarvestPlatform.postBreakEvent(pos, state, player)) return InteractionResult.FAIL;
        if (RightClickHarvestPlatform.postPlaceEvent(pos, player)) return InteractionResult.FAIL;

        dropStacks(state, player, pos);

        Block originalBlock = state.getBlock();

        if (isReplantableAndMature(state)) {
            level.setBlockAndUpdate(pos, getReplantState(state));
        } else if (isSugarCaneOrCactus(state)) {
            level.removeBlock(pos, false);
        }

        wearHoeInHand(player);

        // Regular block breaking causes 0.005f exhaustion
        player.causeFoodExhaustion(0.008f * CONFIG.get().hungerLevel.modifier);

        RightClickHarvestPlatform.postAfterHarvestEvent(player, originalBlock);

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult playSoundClientSide(BlockState state, Player player) {
        player.playSound(getSoundEvent(state), 1.0f, 1.0f);
        return InteractionResult.SUCCESS;
    }

    private static SoundEvent getSoundEvent(BlockState state) {
        return state.getBlock() instanceof NetherWartBlock ? SoundEvents.NETHER_WART_PLANTED : SoundEvents.CROP_PLANTED;
    }

    // Check for hunger, if config requires it
    private static boolean isExhausted(Player player) {
        if (player.hasInfiniteMaterials()) return false;
        if (CONFIG.get().hungerLevel != Config.HungerLevel.NONE) return false;
        return player.getFoodData().getFoodLevel() <= 0;
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

    private static boolean isReplantableAndMature(BlockState state) {
        Block block = state.getBlock();
        return switch (block) {
            case CocoaBlock cocoaBlock -> state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
            case CropBlock cropBlock -> cropBlock.isMaxAge(state);
            case NetherWartBlock netherWartBlock -> state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
            default -> false;
        };
    }

    private static void wearHoeInHand(Player player) {
        ItemStack hoeInHand = player.getMainHandItem();
        if (!isHoeInHand(player)) return;
        hoeInHand.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
    }

    private static boolean isHoeInHand(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        return mainHandItem.is(ItemTags.HOES)
               || mainHandItem.is(LOW_TIER_HOES)
               || mainHandItem.is(MID_TIER_HOES)
               || mainHandItem.is(HIGH_TIER_HOES)
               || RightClickHarvestPlatform.isHoeAccordingToPlatform(mainHandItem);
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

    private static void dropStacks(BlockState state, Player player, BlockPos pos) {
        ServerLevel world = (ServerLevel) player.level();
        List<ItemStack> drops = Block.getDrops(state, world, pos, null, player, player.getMainHandItem());
        Block block = state.getBlock();
        Item replant = block.asItem();
        boolean needToReplant = isReplantable(block);

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
