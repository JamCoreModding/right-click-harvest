package io.github.jamalam360.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Jamalam360
 */

@Mixin(SugarCaneBlock.class)
public class SugarcaneBlockMixin extends AbstractBlockMixin {
    @Override
    public void onUseMixin(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info) {
        BlockPos up = pos.up();

        if (world.getBlockState(up).isOf(Blocks.SUGAR_CANE)) {
            if (!world.isClient) {
                world.breakBlock(up, true);
            } else {
                player.playSound(SoundEvents.ITEM_CROP_PLANT, 1.0f, 1.0f);
            }

            info.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
