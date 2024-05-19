package io.github.jamalam360.rightclickharvest.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.CropBlock;

public class CropsTestSuite {

    private static final BlockPos CROP_CENTRE_POS = new BlockPos(4, 3, 4);

    @GameTest(template = "rightclickharvest-gametest:wheat")
    public void testWoodenHoe(GameTestHelper helper) {
        TestHelper.interact(helper, CROP_CENTRE_POS, Items.WOODEN_HOE.getDefaultInstance());

        helper.succeedIf(() -> {
            helper.assertBlockProperty(CROP_CENTRE_POS, CropBlock.AGE, 0);
            helper.assertItemEntityPresent(Items.WHEAT);
            TestHelper.assertStateInRadius(helper, CROP_CENTRE_POS, 2, false, false, state -> state.getBlock() instanceof CropBlock && state.getValue(CropBlock.AGE) == CropBlock.MAX_AGE);
        });
    }

    @GameTest(template = "rightclickharvest-gametest:wheat")
    public void testIronHoe(GameTestHelper helper) {
        TestHelper.interact(helper, CROP_CENTRE_POS, Items.IRON_HOE.getDefaultInstance());

        helper.succeedIf(() -> {
            helper.assertItemEntityPresent(Items.WHEAT);
            TestHelper.assertStateInRadius(helper, CROP_CENTRE_POS, 1, true, true, state -> state.getBlock() instanceof CropBlock && state.getValue(CropBlock.AGE) == 0);
        });
    }

    @GameTest(template = "rightclickharvest-gametest:wheat")
    public void testDiamondHoe(GameTestHelper helper) {
        TestHelper.interact(helper, CROP_CENTRE_POS, Items.DIAMOND_HOE.getDefaultInstance());

        helper.succeedIf(() -> {
            helper.assertItemEntityPresent(Items.WHEAT);
            TestHelper.assertStateInRadius(helper, CROP_CENTRE_POS, 1, false, true, state -> state.getBlock() instanceof CropBlock && state.getValue(CropBlock.AGE) == 0);
        });
    }

    @GameTest(template = "rightclickharvest-gametest:wheat")
    public void testNetheriteHoe(GameTestHelper helper) {
        TestHelper.interact(helper, CROP_CENTRE_POS, Items.NETHERITE_HOE.getDefaultInstance());

        helper.succeedIf(() -> {
            helper.assertItemEntityPresent(Items.WHEAT);
            TestHelper.assertStateInRadius(helper, CROP_CENTRE_POS, 2, true, true, state -> state.getBlock() instanceof CropBlock && state.getValue(CropBlock.AGE) == 0);
        });
    }

    @GameTest(template = "rightclickharvest-gametest:wheat")
    public void testHoeDurability(GameTestHelper helper) {
        Player player = helper.makeMockSurvivalPlayer();
        TestHelper.interact(helper, player, CROP_CENTRE_POS, Items.WOODEN_HOE.getDefaultInstance());

        helper.succeedIf(() -> {
            helper.assertBlockProperty(CROP_CENTRE_POS, CropBlock.AGE, 0);
            helper.assertItemEntityPresent(Items.WHEAT);

            if (player.getItemInHand(InteractionHand.MAIN_HAND).getMaxDamage() - player.getItemInHand(InteractionHand.MAIN_HAND).getDamageValue() == 0) {
                throw new GameTestAssertException("Hoe durability not decremented correctly");
            }
        });
    }
}
