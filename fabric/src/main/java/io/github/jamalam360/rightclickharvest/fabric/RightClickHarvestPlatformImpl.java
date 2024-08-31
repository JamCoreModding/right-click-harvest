package io.github.jamalam360.rightclickharvest.fabric;

import io.github.jamalam360.rightclickharvest.HarvestContext;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RightClickHarvestPlatformImpl {

    public static void postAfterHarvestEvent(Player player, Block block) {
        RightClickHarvestFabricCallbacks.AFTER_HARVEST.invoker().afterHarvest(new HarvestContext(player, block));
    }

    public static boolean postBreakEvent(BlockPos pos, BlockState state, Player player) {
        Level level = player.level();
        return !PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, pos, state, level.getBlockEntity(pos));
    }

    public static boolean postPlaceEvent(BlockPos pos, Player player) {
        // no-op, fabric doesn't have a specific place block event.
        return false;
    }

    public static boolean isHoeAccordingToPlatform(ItemStack stack) {
        return false;
    }
}
