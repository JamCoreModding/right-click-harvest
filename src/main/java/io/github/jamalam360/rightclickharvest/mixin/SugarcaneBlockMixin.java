/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Jamalam360
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.jamalam360.rightclickharvest.mixin;

import io.github.jamalam360.rightclickharvest.config.Config;
import io.github.jamalam360.rightclickharvest.RightClickHarvestModInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SugarCaneBlock.class)
public abstract class SugarcaneBlockMixin extends AbstractBlockMixin {
    @Override
    public void rightClickHarvest(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info) {
        if (Config.useHunger) {
            if (player.getHungerManager().getFoodLevel() <= 0) {
                return;
            }
        }

        // allow placing sugar cane on top of sugar cane
        if (hit.getSide() == Direction.UP) {
            return;
        }

        int count = 1;

        // find the block the sugar cane stands on
        BlockPos bottom = pos.down();
        while (world.getBlockState(bottom).isOf(Blocks.SUGAR_CANE)) {
            count++;
            bottom = bottom.down();
        }

        // when the sugar cane is only 1 tall, do nothing
        if (count == 1 && !world.getBlockState(pos.up()).isOf(Blocks.SUGAR_CANE)) {
            return;
        }

        // else break the 2nd from bottom cane
        if (!world.isClient) {
            world.breakBlock(bottom.up(2), true);

            if (Config.useHunger && player.world.random.nextBoolean()) {
                player.addExhaustion(2f);
            }
        } else {
            player.playSound(SoundEvents.ITEM_CROP_PLANT, 1.0f, 1.0f);
        }

        info.setReturnValue(ActionResult.SUCCESS);
    }
}
