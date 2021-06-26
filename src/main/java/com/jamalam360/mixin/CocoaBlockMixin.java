package com.jamalam360.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CocoaBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CocoaBlock.class)
public abstract class CocoaBlockMixin extends CropBlockMixin {
	@Override
	protected void handlerMethod(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info) {
		if (state.get(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE) {
			if (!world.isClient) {
				world.setBlockState(pos, state.with(CocoaBlock.AGE, 0), 2);
				Block.dropStacks(state, world, pos, null, player, player.getStackInHand(hand));
			} else {
				player.playSound(SoundEvents.ITEM_CROP_PLANT, 1.0f, 1.0f);
			}

			info.setReturnValue(ActionResult.SUCCESS);
		}
	}
}
