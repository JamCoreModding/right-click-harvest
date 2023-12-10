package io.github.jamalam360.rightclickharvest.fabric;

import io.github.jamalam360.rightclickharvest.fabriclike.RightClickHarvestFabricLike;
import net.fabricmc.api.ModInitializer;

public class RightClickHarvestFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		RightClickHarvestFabricLike.init();
	}
}
