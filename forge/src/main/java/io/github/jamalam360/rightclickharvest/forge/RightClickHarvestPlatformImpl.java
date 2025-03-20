package io.github.jamalam360.rightclickharvest.forge;

import io.github.jamalam360.rightclickharvest.HarvestContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.level.BlockEvent;

public class RightClickHarvestPlatformImpl {
	public static void postAfterHarvestEvent(HarvestContext context) {
		RightClickHarvestForgeEvents.AfterHarvest.post(context);
	}

	public static boolean postBreakEvent(Level level, BlockPos pos, BlockState state, Player player) {
		BlockEvent.BreakEvent breakEv = new BlockEvent.BreakEvent(level, pos, state, player);
		return MinecraftForge.EVENT_BUS.post(breakEv);
	}

	public static boolean postPlaceEvent(Level level, BlockPos pos, Player player) {
		BlockEvent.EntityPlaceEvent placeEv = new BlockEvent.EntityPlaceEvent(
				BlockSnapshot.create(level.dimension(), level, pos),
				level.getBlockState(pos.below()),
				player
		);
		return MinecraftForge.EVENT_BUS.post(placeEv);
	}

	public static boolean isHoeAccordingToPlatform(ItemStack stack) {
		return stack.canPerformAction(ToolActions.HOE_TILL);
	}
}
