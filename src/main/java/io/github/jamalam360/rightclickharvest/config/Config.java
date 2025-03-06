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

package io.github.jamalam360.rightclickharvest.config;

import io.github.jamalam360.rightclickharvest.RightClickHarvestModInit;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * @author Jamalam360
 */
@Mod.EventBusSubscriber(modid = RightClickHarvestModInit.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue REQUIRE_HOE = BUILDER
            .comment("Only allow harvesting certain crops (e.g. wheat, carrots) if the player is holding a hoe in their hand.")
            .define("requireHoe", false);

    private static final ForgeConfigSpec.BooleanValue HARVEST_IN_RADIUS = BUILDER
            .comment("Make hoes of different tiers harvest multiple blocks in a radius.")
            .define("harvestInRadius", true);

    private static final ForgeConfigSpec.EnumValue<HungerLevel> HUNGER_LEVEL = BUILDER
            .comment("The food usage when harvesting crops.")
            .defineEnum("hungerLevel", HungerLevel.NORMAL);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean requireHoe;
    public static boolean harvestInRadius;
    public static HungerLevel hungerLevel;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        requireHoe = REQUIRE_HOE.get();
        harvestInRadius = HARVEST_IN_RADIUS.get();
        hungerLevel = HUNGER_LEVEL.get();
    }

    public enum HungerLevel {
        NONE(0.0f),
        LOW(0.5f),
        NORMAL(1.0f),
        HIGH(2.0f);

        public final float modifier;

        HungerLevel(float modifier) {
            this.modifier = modifier;
        }
    }
}
