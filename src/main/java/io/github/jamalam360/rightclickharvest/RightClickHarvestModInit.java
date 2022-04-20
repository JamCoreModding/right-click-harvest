package io.github.jamalam360.rightclickharvest;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;

/**
 * @author Jamalam360
 */
public class RightClickHarvestModInit implements ModInitializer, ModMenuApi {
    @Override
    public void onInitialize() {
        MidnightConfig.init("rightclickharvest", Config.class);
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> MidnightConfig.getScreen(parent, "rightclickharvest");
    }

    public static boolean canRightClickHarvest(PlayerEntity player) {
        return !Config.requireHoe || player.getMainHandStack().getItem() instanceof HoeItem;
    }
}
