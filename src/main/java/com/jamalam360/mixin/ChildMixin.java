package com.jamalam360.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.CropBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(CropBlock.class)
public abstract class ChildMixin extends CropBlockMixin {
    @Override
    protected void handlerMethod(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit, CallbackInfoReturnable<ActionResult> info) {
        CropBlock instance = (CropBlock) (Object) this;

        if (instance.isMature(state) && !world.isClient) {
            world.setBlockState(pos, instance.withAge(0), 2);

            instance.dropStacks(state, world, pos, null, player, player.getStackInHand(hand));
        }
    }
}
