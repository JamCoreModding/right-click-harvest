package io.github.jamalam360.rightclickharvest.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.CocoaBlock;

public class CocoaBeanTestSuite {

    private static final BlockPos COCOA_BEANS_POS = new BlockPos(0, 1, 1);

    @GameTest(structure = "rightclickharvest-gametest:cocoa_beans")
    public void testCocoaBeans(GameTestHelper helper) {
        TestHelper.interact(helper, COCOA_BEANS_POS, ItemStack.EMPTY);

        helper.succeedIf(() -> {
            helper.assertBlockProperty(COCOA_BEANS_POS, CocoaBlock.AGE, 0);
            helper.assertItemEntityPresent(Items.COCOA_BEANS);
        });
    }
}
