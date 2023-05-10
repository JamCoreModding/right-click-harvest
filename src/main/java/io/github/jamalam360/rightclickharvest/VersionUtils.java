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

package io.github.jamalam360.rightclickharvest;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

public class VersionUtils {
    static {
        VersionUtils.init();
    }

    public static TagKey<Item> HOES;
    public static RegistryKey<Registry<Block>> BLOCK_KEY;
    public static RegistryKey<Registry<Item>> ITEM_KEY;

    private static void init() {
        try {
            RightClickHarvestModInit.LOGGER.info("Initializing VersionUtils");
            Version version = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion();

            VersionPredicate gte1194 = VersionPredicate.parse(">=1.19.4");
            VersionPredicate gte1193 = VersionPredicate.parse(">=1.19.3");

            if (gte1194.test(version)) {
                Class<?> clazz = Class.forName("net.minecraft.registry.tag.ItemTags");
                HOES = (TagKey<Item>) clazz.getDeclaredField("HOES").get(null);
            } else {
                Class<?> clazz = Class.forName("net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags");
                HOES = (TagKey<Item>) clazz.getDeclaredField("HOES").get(null);
            }

            if (gte1193.test(version)) {
                Class<?> clazz = Class.forName("net.minecraft.registry.Registries");

                Object block = clazz.getDeclaredField("BLOCK").get(null);
                Object item = clazz.getDeclaredField("ITEM").get(null);

                BLOCK_KEY = (RegistryKey<Registry<Block>>) block.getClass().getDeclaredMethod("getKey").invoke(block);
                ITEM_KEY = (RegistryKey<Registry<Item>>) item.getClass().getDeclaredMethod("getKey").invoke(item);
            } else {
                Class<?> clazz = Class.forName("net.minecraft.util.registry.Registry");

                BLOCK_KEY = (RegistryKey<Registry<Block>>) clazz.getDeclaredField("BLOCK_KEY").get(null);
                ITEM_KEY = (RegistryKey<Registry<Item>>) clazz.getDeclaredField("ITEM_KEY").get(null);
            }

        } catch (Exception e) {
            RightClickHarvestModInit.LOGGER.error("Failed to initialize VersionUtils. This will cause issues later");
            e.printStackTrace();
        }

        RightClickHarvestModInit.LOGGER.info("VersionUtils initialized successfully");
    }
}
