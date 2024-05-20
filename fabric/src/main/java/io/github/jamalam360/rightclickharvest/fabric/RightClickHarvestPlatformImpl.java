package io.github.jamalam360.rightclickharvest.fabric;

import io.github.jamalam360.rightclickharvest.HarvestContext;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RightClickHarvestPlatformImpl {

    public static void postAfterHarvestEvent(HarvestContext context) {
        RightClickHarvestFabricCallbacks.AFTER_HARVEST.invoker().afterHarvest(context);
    }

    public static boolean postBreakEvent(Level level, BlockPos pos, BlockState state, Player player) {
        return !PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, pos, state, level.getBlockEntity(pos));
    }

    public static boolean postPlaceEvent(Level level, BlockPos pos, Player player) {
        // no-op, fabric doesn't have a specific place block event.
        return false;
    }
}
