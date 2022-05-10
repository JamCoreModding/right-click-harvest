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

import io.github.jamalam360.rightclickharvest.config.Config;
import io.github.jamalam360.rightclickharvest.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import org.apache.logging.log4j.LogManager;

/**
 * @author Jamalam360
 */
public class RightClickHarvestModInit implements ModInitializer {
    @Override
    public void onInitialize() {
        LogManager.getLogger("RightClickHarvest/Initializer").info("Initializing RightClickHarvest...");
        MidnightConfig.init("rightclickharvest", Config.class);
    }

    public static boolean canRightClickHarvest(PlayerEntity player) {
        return (
                Config.requireHoe &&
                (player.getMainHandStack().isIn(ConventionalItemTags.HOES) || player.getMainHandStack().getItem() instanceof HoeItem)
        )
                || (
                        !Config.requireHoe && player.getMainHandStack().isEmpty()
        );
    }
}
