package io.github.jamalam360.rightclickharvest.gametest;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.UUID;

public class StatsTestSuite {
	private static final BlockPos CROP_CENTRE_POS = new BlockPos(4, 2, 4);
	private final HashMap<Stat<?>, Integer> stats = new HashMap<>();

	@GameTest(structure = "rightclickharvest-gametest:wheat")
	public void testWheatStats(GameTestHelper helper) {
		Player player = makeMockPlayerWithStats(helper.getLevel());
		TestHelper.interact(helper, player, CROP_CENTRE_POS, Items.WOODEN_HOE.getDefaultInstance());

		helper.succeedIf(() -> {
			if (stats.getOrDefault(Stats.ITEM_USED.get(Items.WOODEN_HOE), 0) != 1) {
				throw helper.assertionException(Component.literal("ITEM_USED#woodenHoe was not incremented"));
			}

			if (stats.getOrDefault(Stats.BLOCK_MINED.get(Blocks.WHEAT), 0) != 1) {
				throw helper.assertionException(Component.literal("BLOCK_MINED#wheat was not incremented"));
			}

			if (stats.getOrDefault(Stats.ITEM_USED.get(Items.WHEAT_SEEDS), 0) != 1) {
				throw helper.assertionException(Component.literal("ITEM_USED#wheatSeeds was not incremented"));
			}
		});
	}

	private Player makeMockPlayerWithStats(Level level) {
		return new Player(level, new GameProfile(UUID.randomUUID(), "test-mock-player")) {
			@Override
			public GameType gameMode() {
				return GameType.CREATIVE;
			}

			@Override
			public boolean isClientAuthoritative() {
				return false;
			}

			@Override
			public void awardStat(@NonNull Stat<?> stat) {
				stats.put(stat, stats.getOrDefault(stat, 0) + 1);
			}
		};
	}
}
