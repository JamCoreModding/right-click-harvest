package io.github.jamalam360.rightclickharvest.gametest;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.UUID;

public class StatsTestSuite {
	private static final BlockPos CROP_CENTRE_POS = new BlockPos(4, 2, 4);
	private final HashMap<Stat<?>, Integer> stats = new HashMap<>();

	@GameTest(template = "rightclickharvest-gametest:wheat")
	public void testWheatStats(GameTestHelper helper) {
		Player player = makeMockPlayerWithStats(helper.getLevel(), CROP_CENTRE_POS);
		TestHelper.interact(helper, player, CROP_CENTRE_POS, Items.WOODEN_HOE.getDefaultInstance());

		helper.succeedIf(() -> {
			if (stats.getOrDefault(Stats.ITEM_USED.get(Items.WOODEN_HOE), 0) != 1) {
				throw new AssertionError("ITEM_USED#woodenHoe was not incremented");
			}

			if (stats.getOrDefault(Stats.BLOCK_MINED.get(Blocks.WHEAT), 0) != 1) {
				throw new AssertionError("BLOCK_MINED#wheat was not incremented");
			}

			if (stats.getOrDefault(Stats.ITEM_USED.get(Items.WHEAT_SEEDS), 0) != 1) {
				throw new AssertionError("ITEM_USED#wheatSeeds was not incremented");
			}
		});
	}

	private Player makeMockPlayerWithStats(Level level, BlockPos pos) {
		return new Player(level, pos, 0, new GameProfile(UUID.randomUUID(), "test-mock-player")) {
			@Override
			public void awardStat(Stat<?> stat) {
				stats.put(stat, stats.getOrDefault(stat, 0) + 1);
			}

			@Override
			public boolean isSpectator() {
				return false;
			}

			@Override
			public boolean isCreative() {
				return false;
			}
		};
	}
}
