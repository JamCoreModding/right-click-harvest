/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Jamalam360
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

package io.github.jamalam360.rightclickharvest.compat;

import java.lang.reflect.Method;

import io.github.jamalam360.rightclickharvest.RightClickHarvestCallbacks;
import io.github.jamalam360.rightclickharvest.RightClickHarvestModInit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class RpgStatsGte5 implements ModInitializer {
    private static final Identifier FARMING = new Identifier("rpgstats:farming");
    private Object levelUtils = null;
    private Method addXpAndLevelUp = null;

    @Override
    public void onInitialize() {
        try {
            Class<?> clazz = Class.forName("io.github.silverandro.rpgstats.LevelUtils");
            levelUtils = clazz.getField("INSTANCE").get(null);
            addXpAndLevelUp = clazz.getMethod("addXpAndLevelUp", Identifier.class, ServerPlayerEntity.class,
                    int.class);

            RightClickHarvestCallbacks.AFTER_HARVEST.register((player, block) -> {
                if (player instanceof ServerPlayerEntity serverPlayerEntity) {
                    try {
                        addXpAndLevelUp.invoke(levelUtils, FARMING, serverPlayerEntity, 1);
                    } catch (Exception e) {
                        RightClickHarvestModInit.LOGGER.error("Failed to call RPGStats methods");
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            RightClickHarvestModInit.LOGGER.error("Failed to enable RPGStats compatibility");
            e.printStackTrace();
        }
    }
}
