package io.github.jamalam360.rightclickharvest;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RightClickHarvestPlatform {
	@ExpectPlatform
	public static void postAfterHarvestEvent(HarvestContext context) {
	}

	@ExpectPlatform
	public static boolean postBreakEvent(Level level, BlockPos pos, BlockState state, Player player) {
		return false;
	}

	@ExpectPlatform
	public static boolean postPlaceEvent(Level level, BlockPos pos, Player player) {
		return false;
	}
}
