package io.github.jamalam360.rightclickharvest;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

/**
 * @param player The player who harvested the block
 * @param block The block that was harvested
 */
public record HarvestContext(Player player, Block block) {
}
