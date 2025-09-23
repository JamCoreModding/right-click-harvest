package io.github.jamalam360.rightclickharvest.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

// Cacti and sugarcane
public class TallPlantsTestSuite {

    private static final BlockPos SUGARCANE_BOTTOM_POS = new BlockPos(2, 2, 1);

    @GameTest(structure = "rightclickharvest-gametest:sugarcane_tall")
    public void testTallBottom(GameTestHelper helper) {
        TestHelper.interact(helper, SUGARCANE_BOTTOM_POS, Items.WOODEN_HOE.getDefaultInstance());

        helper.succeedIf(() -> {
            this.assertHeight(helper, 1);
            helper.assertItemEntityPresent(Items.SUGAR_CANE);
        });
    }

    @GameTest(structure = "rightclickharvest-gametest:sugarcane_tall")
    public void testTallAbove(GameTestHelper helper) {
        TestHelper.interact(helper, SUGARCANE_BOTTOM_POS.above(1), Items.WOODEN_HOE.getDefaultInstance());

        helper.succeedIf(() -> {
            this.assertHeight(helper, 1);
            helper.assertItemEntityPresent(Items.SUGAR_CANE);
        });
    }

    @GameTest(structure = "rightclickharvest-gametest:sugarcane_short")
    public void testShort(GameTestHelper helper) {
        TestHelper.interact(helper, SUGARCANE_BOTTOM_POS, ItemStack.EMPTY);

        helper.succeedIf(() -> {
            this.assertHeight(helper, 1);
            helper.assertEntityNotPresent(EntityType.ITEM);
        });
    }

    private void assertHeight(GameTestHelper helper, int height) {
        int actualHeight = 0;

        for (int i = 0; i < height; i++) {
            if (helper.getBlockState(SUGARCANE_BOTTOM_POS.above(i)).getBlock() == Blocks.SUGAR_CANE) {
                actualHeight++;
            } else {
                break;
            }
        }

        helper.assertTrue(actualHeight == height, Component.literal("Expected sugarcane to be " + height + " blocks tall, but it was " + actualHeight + " blocks tall"));
    }
}
