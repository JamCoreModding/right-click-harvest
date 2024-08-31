package io.github.jamalam360.rightclickharvest.neoforge;

import io.github.jamalam360.rightclickharvest.HarvestContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;

public class RightClickHarvestPlatformImpl {
	public static void postAfterHarvestEvent(Player player, Block block) {
		RightClickHarvestNeoForgeEvents.AfterHarvest.post(new HarvestContext(player, block));
	}

	public static boolean postBreakEvent(BlockPos pos, BlockState state, Player player) {
		Level level = player.level();
		BlockEvent.BreakEvent breakEv = new BlockEvent.BreakEvent(level, pos, state, player);
		return NeoForge.EVENT_BUS.post(breakEv).isCanceled();
	}

	public static boolean postPlaceEvent(BlockPos pos, Player player) {
		Level level = player.level();
		BlockEvent.EntityPlaceEvent placeEv = new BlockEvent.EntityPlaceEvent(
				BlockSnapshot.create(level.dimension(), level, pos),
				level.getBlockState(pos.below()),
				player
		);
		return NeoForge.EVENT_BUS.post(placeEv).isCanceled();
	}

	public static boolean isHoeAccordingToPlatform(ItemStack stack) {
		return stack.canPerformAction(ItemAbilities.HOE_TILL);
	}
}
