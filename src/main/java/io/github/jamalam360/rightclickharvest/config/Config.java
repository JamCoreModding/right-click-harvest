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

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

/**
 * @author Jamalam360
 */
public class Config {
    public final ConfigValue<Boolean> requireHoe;
    public final ConfigValue<Boolean> harvestInRadius;
    public final ConfigValue<HungerLevel> hungerLevel;

    public Config(ForgeConfigSpec.Builder builder) {
        requireHoe = builder.define("requireHoe", true);
        harvestInRadius = builder.define("harvestInRadius", true);
        hungerLevel = builder.defineEnum("hungerLevel", HungerLevel.NORMAL);
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
