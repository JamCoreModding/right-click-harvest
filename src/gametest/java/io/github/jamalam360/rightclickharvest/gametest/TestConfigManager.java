package io.github.jamalam360.rightclickharvest.gametest;

import io.github.jamalam360.rightclickharvest.config.Config;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

public class TestConfigManager {
    public static void apply(Method method) {
        if (method.isAnnotationPresent(UseHunger.class)) {
            Config.useHunger = true;
        }

        if (method.isAnnotationPresent(RequireHoe.class)) {
            Config.requireHoe = true;
        }

        if (method.isAnnotationPresent(HarvestInRadius.class)) {
            Config.harvestInRadius = true;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface UseHunger {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface RequireHoe {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface HarvestInRadius {
    }
}
