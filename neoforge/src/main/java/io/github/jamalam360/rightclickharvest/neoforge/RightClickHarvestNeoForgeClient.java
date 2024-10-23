package io.github.jamalam360.rightclickharvest.neoforge;

import io.github.jamalam360.rightclickharvest.RightClickHarvest;
import io.github.jamalam360.rightclickharvest.RightClickHarvestClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = RightClickHarvest.MOD_ID, dist = Dist.CLIENT)
public class RightClickHarvestNeoForgeClient {
	public RightClickHarvestNeoForgeClient() {
		RightClickHarvestClient.init();
	}
}
