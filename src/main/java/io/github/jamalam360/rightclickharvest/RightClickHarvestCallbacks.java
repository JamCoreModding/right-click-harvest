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

package io.github.jamalam360.rightclickharvest;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class RightClickHarvestCallbacks {

    public static final Event<OnHarvestCallback> ON_HARVEST = EventFactory.createArrayBacked(OnHarvestCallback.class, (callbacks -> (player, pos, state) -> {
        for (OnHarvestCallback callback : callbacks) {
            callback.onHarvest(player, pos, state);
        }
    }));
    public static final Event<AfterHarvestCallback> AFTER_HARVEST = EventFactory.createArrayBacked(AfterHarvestCallback.class, (callbacks -> (player, block) -> {
        for (AfterHarvestCallback callback : callbacks) {
            callback.afterHarvest(player, block);
        }
    }));

    public interface OnHarvestCallback {

        /**
         * Called just before right-click-harvesting.
         */
        void onHarvest(PlayerEntity entity, BlockPos pos, BlockState state);
    }

    public interface AfterHarvestCallback {

        /**
         * Called after right-click-harvesting.
         *
         * @param block The block that was harvested.
         */
        void afterHarvest(PlayerEntity entity, Block block);
    }
}
