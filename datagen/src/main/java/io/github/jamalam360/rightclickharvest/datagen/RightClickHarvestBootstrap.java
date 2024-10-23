package io.github.jamalam360.rightclickharvest.datagen;

import io.github.jamalam360.rightclickharvest.RightClickHarvest;
import net.fabricmc.api.ModInitializer;

public class RightClickHarvestBootstrap implements ModInitializer {
	@Override
	public void onInitialize() {
		RightClickHarvest.init();
	}
}
