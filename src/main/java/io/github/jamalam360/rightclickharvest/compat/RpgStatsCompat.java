package io.github.jamalam360.rightclickharvest.compat;

import io.github.jamalam360.rightclickharvest.RightClickHarvestCallbacks;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RpgStatsCompat implements ModInitializer {

    @Override
    public void onInitialize() {
        try {
            Class<?> clazz = Class.forName("io.github.silverandro.rpgstats.EventsKt");
            Method grantBlockBreakXP = clazz.getDeclaredMethod("grantBlockBreakXP", World.class, ServerPlayerEntity.class, BlockPos.class, BlockState.class);

            RightClickHarvestCallbacks.ON_HARVEST.register((player, pos, state) -> {
                if (player instanceof ServerPlayerEntity serverPlayerEntity) {
                    try {
                        grantBlockBreakXP.invoke(null, player.getWorld(), serverPlayerEntity, pos, state);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception ignored) {
        }
    }
}
