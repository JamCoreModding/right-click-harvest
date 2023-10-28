package io.github.jamalam360.rightclickharvest.fabric;

import io.github.jamalam360.rightclickharvest.fabriclike.RightClickHarvestFabricLike;
import io.github.jamalam360.rightclickharvest.fabriclike.RightClickHarvestFabricLikeCallbacks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import java.util.Random;

public class RightClickHarvestFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        RightClickHarvestFabricLike.init();
    }
}
