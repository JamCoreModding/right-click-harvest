package io.github.jamalam360.rightclickharvest.fabric;

import io.github.jamalam360.rightclickharvest.RightClickHarvestClient;
import net.fabricmc.api.ClientModInitializer;

public class RightClickHarvestClientFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RightClickHarvestClient.init();
	}
}
