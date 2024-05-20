package io.github.jamalam360.rightclickharvest.gametest;

import io.github.jamalam360.rightclickharvest.Config.HungerLevel;
import io.github.jamalam360.rightclickharvest.RightClickHarvest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.CropBlock;

public class ConfigTestSuite {

    private static final BlockPos CROP_CENTRE_POS = new BlockPos(4, 3, 4);

    @GameTest(template = "rightclickharvest-gametest:wheat")
    public void testRequireHoeTrueFail(GameTestHelper helper) {
        TestHelper.interact(helper, CROP_CENTRE_POS, ItemStack.EMPTY);

        helper.succeedIf(() -> {
            helper.assertBlockProperty(CROP_CENTRE_POS, CropBlock.AGE, CropBlock.MAX_AGE);
            helper.assertItemEntityNotPresent(Items.WHEAT);
        });
    }

    @GameTest(template = "rightclickharvest-gametest:wheat")
    public void testRequireHoeTrueSucceed(GameTestHelper helper) {
        TestHelper.interact(helper, CROP_CENTRE_POS, Items.WOODEN_HOE.getDefaultInstance());

        helper.succeedIf(() -> {
            helper.assertBlockProperty(CROP_CENTRE_POS, CropBlock.AGE, 0);
            helper.assertItemEntityPresent(Items.WHEAT);
        });
    }

    @GameTest(template = "rightclickharvest-gametest:wheat")
    public void testRequireHoeFalseWithHand(GameTestHelper helper) {
        RightClickHarvest.CONFIG.get().requireHoe = false;
        TestHelper.interact(helper, CROP_CENTRE_POS, ItemStack.EMPTY);

        helper.succeedIf(() -> {
            helper.assertBlockProperty(CROP_CENTRE_POS, CropBlock.AGE, 0);
            helper.assertItemEntityPresent(Items.WHEAT);
            RightClickHarvest.CONFIG.get().requireHoe = true;
        });
    }

    @GameTest(template = "rightclickharvest-gametest:wheat")
    public void testRequireHoeFalseWithHoe(GameTestHelper helper) {
        RightClickHarvest.CONFIG.get().requireHoe = false;
        TestHelper.interact(helper, CROP_CENTRE_POS, Items.WOODEN_HOE.getDefaultInstance());

        helper.succeedIf(() -> {
            helper.assertBlockProperty(CROP_CENTRE_POS, CropBlock.AGE, 0);
            helper.assertItemEntityPresent(Items.WHEAT);
            RightClickHarvest.CONFIG.get().requireHoe = true;
        });
    }

    @GameTest(template = "rightclickharvest-gametest:wheat")
    public void testHarvestInRadiusFalse(GameTestHelper helper) {
        RightClickHarvest.CONFIG.get().harvestInRadius = false;
        TestHelper.interact(helper, CROP_CENTRE_POS, Items.NETHERITE_HOE.getDefaultInstance());

        helper.succeedIf(() -> {
            helper.assertItemEntityPresent(Items.WHEAT);
            TestHelper.assertStateInRadius(helper, CROP_CENTRE_POS, 2, true, true, state -> state.getBlock() instanceof CropBlock && state.getValue(CropBlock.AGE) == CropBlock.MAX_AGE);
            RightClickHarvest.CONFIG.get().harvestInRadius = true;
        });
    }

    @GameTest(template = "rightclickharvest-gametest:wheat")
    public void testHungerLevelNormal(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        float exhaustion = player.getFoodData().getExhaustionLevel();
        TestHelper.interact(helper, player, CROP_CENTRE_POS, Items.WOODEN_HOE.getDefaultInstance());

        helper.succeedIf(() -> {
            helper.assertBlockProperty(CROP_CENTRE_POS, CropBlock.AGE, 0);
            helper.assertItemEntityPresent(Items.WHEAT);

            if (player.getFoodData().getExhaustionLevel() <= exhaustion) {
                throw new AssertionError("Player's exhaustion level did not increase");
            }
        });
    }

    @GameTest(template = "rightclickharvest-gametest:wheat")
    public void testHungerLevelNone(GameTestHelper helper) {
        RightClickHarvest.CONFIG.get().hungerLevel = HungerLevel.NONE;
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        float exhaustion = player.getFoodData().getExhaustionLevel();
        TestHelper.interact(helper, player, CROP_CENTRE_POS, Items.WOODEN_HOE.getDefaultInstance());

        helper.succeedIf(() -> {
            helper.assertBlockProperty(CROP_CENTRE_POS, CropBlock.AGE, 0);
            helper.assertItemEntityPresent(Items.WHEAT);

            RightClickHarvest.CONFIG.get().hungerLevel = HungerLevel.NORMAL;

            if (player.getFoodData().getExhaustionLevel() > exhaustion) {
                throw new AssertionError("Player's exhaustion level increased when it shouldn't have");
            }
        });
    }
}
