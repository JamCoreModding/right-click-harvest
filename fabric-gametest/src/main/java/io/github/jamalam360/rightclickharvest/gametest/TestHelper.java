package io.github.jamalam360.rightclickharvest.gametest;

import io.github.jamalam360.rightclickharvest.RightClickHarvest;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TestHelper {

    public static void interact(GameTestHelper helper, BlockPos pos, ItemStack stack) {
        interact(helper, helper.makeMockPlayer(GameType.CREATIVE), pos, stack);
    }

    public static void interact(GameTestHelper helper, Player player, BlockPos pos, ItemStack stack) {
        pos = helper.absolutePos(pos);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockHitResult hitResult = new BlockHitResult(Vec3.atLowerCornerOf(pos), Direction.NORTH, pos, false);
        RightClickHarvest.onBlockUse(player, InteractionHand.MAIN_HAND, hitResult, true);
    }

    public static void assertStateInRadius(GameTestHelper helper, BlockPos center, int radius, boolean circle, boolean includeCentre, Predicate<BlockState> predicate) {
        if (radius == 1 && circle) {
            for (Direction dir : RightClickHarvest.CARDINAL_DIRECTIONS) {
                BlockPos pos = center.relative(dir);

                if (!includeCentre && pos.equals(center)) {
                    continue;
                }

                if (!predicate.test(helper.getBlockState(pos))) {
                    throw new GameTestAssertException("Block at " + pos + " does not match predicate (" + helper.getBlockState(pos) + ")");
                }
            }
        } else if (radius > 0) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x == 0 && z == 0) {
                        continue;
                    }

                    BlockPos pos = center.relative(Direction.Axis.X, x).relative(Direction.Axis.Z, z);

                    if (!includeCentre && pos.equals(center)) {
                        continue;
                    }

                    if (circle && pos.distManhattan(center) > radius) {
                        continue;
                    }

                    if (!predicate.test(helper.getBlockState(pos))) {
                        throw new GameTestAssertException("Block at " + pos + " does not match predicate (" + helper.getBlockState(pos) + ")");
                    }
                }
            }
        }
    }
}
