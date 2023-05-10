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
import net.fabricmc.loader.api.MappingResolver;
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

    private static final String CLASS_ITEM_TAGS = "net.minecraft.class_3489";
    private static final String CLASS_REGISTRIES = "net.minecraft.class_7923";
    private static final String CLASS_DEFAULTED_REGISTRY = "net.minecraft.class_7922";
    private static final String CLASS_REGISTRY = "net.minecraft.class_2378";
    private static final String CLASS_TAG_KEY = "net.minecraft.class_6862";
    private static final String CLASS_REGISTRY_KEY = "net.minecraft.class_5321";
    private static final String CLASS_CONVENTIONAL_ITEM_TAGS = "net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags";
    private static final String FIELD_HOES = "field_42613";
    private static final String FIELD_BLOCK = "field_41175";
    private static final String FIELD_ITEM = "field_41178";
    private static final String FIELD_BLOCK_KEY = "field_25105";
    private static final String FIELD_ITEM_KEY = "field_25108";
    private static final String METHOD_GET_KEY = "method_30517";

    private static void init() {
        try {
            RightClickHarvestModInit.LOGGER.info("Initializing VersionUtils");

            MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();

            Version version = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion();

            VersionPredicate gte1194 = VersionPredicate.parse(">=1.19.4");
            VersionPredicate gte1193 = VersionPredicate.parse(">=1.19.3");

            if (gte1194.test(version)) {
                Class<?> clazz = Class.forName(resolver.mapClassName("intermediary", CLASS_ITEM_TAGS));
                HOES = (TagKey<Item>) clazz.getField(resolver.mapFieldName("intermediary", CLASS_ITEM_TAGS, FIELD_HOES, classToDesc(CLASS_TAG_KEY))).get(null);
            } else {
                Class<?> clazz = Class.forName(CLASS_CONVENTIONAL_ITEM_TAGS);
                HOES = (TagKey<Item>) clazz.getField("HOES").get(null);
            }

            if (gte1193.test(version)) {
                Class<?> clazz = Class.forName(resolver.mapClassName("intermediary", CLASS_REGISTRIES));

                Object block = clazz.getDeclaredField(resolver.mapFieldName("intermediary", CLASS_REGISTRIES,
                        FIELD_BLOCK, classToDesc(CLASS_DEFAULTED_REGISTRY))).get(null);
                Object item = clazz.getDeclaredField(resolver.mapFieldName("intermediary", CLASS_REGISTRIES, FIELD_ITEM,
                        classToDesc(CLASS_DEFAULTED_REGISTRY))).get(null);

                String getKey = resolver.mapMethodName("intermediary", CLASS_REGISTRY, METHOD_GET_KEY,
                        "()" + classToDesc(CLASS_REGISTRY_KEY));

                BLOCK_KEY = (RegistryKey<Registry<Block>>) block.getClass().getMethod(getKey).invoke(block);
                ITEM_KEY = (RegistryKey<Registry<Item>>) item.getClass().getMethod(getKey).invoke(item);
            } else {
                Class<?> clazz = Class.forName(resolver.mapClassName("intermediary", CLASS_REGISTRY));

                BLOCK_KEY = (RegistryKey<Registry<Block>>) clazz
                        .getField(resolver.mapFieldName("intermediary", CLASS_REGISTRY, FIELD_BLOCK_KEY,
                                classToDesc(CLASS_REGISTRY_KEY)))
                        .get(null);
                ITEM_KEY = (RegistryKey<Registry<Item>>) clazz
                        .getField(resolver.mapFieldName("intermediary", CLASS_REGISTRY, FIELD_ITEM_KEY,
                                classToDesc(CLASS_REGISTRY_KEY)))
                        .get(null);
            }

            RightClickHarvestModInit.LOGGER.info("VersionUtils initialized successfully");
        } catch (Exception e) {
            RightClickHarvestModInit.LOGGER.error("Failed to initialize VersionUtils. This will cause issues later");
            e.printStackTrace();
        }
    }
    
    private static String classToDesc(String desc) {
        return "L" + desc.replaceAll("\\.", "/") + ";";
    }
}
