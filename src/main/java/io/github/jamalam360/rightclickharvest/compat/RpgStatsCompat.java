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

package io.github.jamalam360.rightclickharvest.compat;

import io.github.jamalam360.rightclickharvest.RightClickHarvestCallbacks;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RpgStatsCompat implements ModInitializer {

    @Override
    public void onInitialize() {
        try {
            if (
                  VersionPredicate.parse("<5.0.0").test(FabricLoader.getInstance().getModContainer("rpgstats").get().getMetadata().getVersion())
            ) {
                Class<?> clazz = Class.forName("mc.rpgstats.main.RPGStats");
                Method addXpAndLevelUp = clazz.getDeclaredMethod("addXpAndLevelUp", Identifier.class, ServerPlayerEntity.class, int.class);

                RightClickHarvestCallbacks.AFTER_HARVEST.register((player, block) -> {
                    if (player instanceof ServerPlayerEntity serverPlayerEntity) {
                        try {
                            addXpAndLevelUp.invoke(null, new Identifier("rpgstats:farming"), serverPlayerEntity, 1);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } else {
                Class<?> clazz = Class.forName("io.github.silverandro.rpgstats.EventsKt");
                Method grantBlockBreakXP = clazz.getDeclaredMethod("grantBlockBreakXP", World.class, PlayerEntity.class, BlockPos.class, BlockState.class);

                RightClickHarvestCallbacks.ON_HARVEST.register((player, pos, state) -> {
                    try {
                        grantBlockBreakXP.invoke(null, player.getWorld(), player, pos, state);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception ignored) {
            throw new RuntimeException(ignored);
        }
    }
}
