package io.github.jamalam360.rightclickharvest.fabric;

import io.github.jamalam360.rightclickharvest.HarvestContext;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RightClickHarvestPlatformImpl {
    public static void postAfterHarvestEvent(HarvestContext ctx, Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
        PlayerBlockBreakEvents.AFTER.invoker().afterBlockBreak(level, ctx.player(), pos, state, be);
        RightClickHarvestFabricCallbacks.AFTER_HARVEST.invoker().afterHarvest(ctx);
    }

    public static boolean postBreakEvent(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        boolean allowed = PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, pos, state, be);
        if (!allowed) {
            PlayerBlockBreakEvents.CANCELED.invoker().onBlockBreakCanceled(level, player, pos, state, be);
        }
        return !allowed;
    }

    public static boolean postPlaceEvent(Level level, BlockPos pos, Player player) {
        // no-op, fabric doesn't have a specific place block event.
        return false;
    }
}
