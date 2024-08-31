package io.github.jamalam360.rightclickharvest;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RightClickHarvestPlatform {
	@ExpectPlatform
	public static void postAfterHarvestEvent(Player player, Block block) {
	}

	@ExpectPlatform
	public static boolean postBreakEvent(BlockPos pos, BlockState state, Player player) {
		return false;
	}

	@ExpectPlatform
	public static boolean postPlaceEvent(BlockPos pos, Player player) {
		return false;
	}

	@ExpectPlatform
	public static boolean isHoeAccordingToPlatform(ItemStack stack) {
		return false;
	}
}
