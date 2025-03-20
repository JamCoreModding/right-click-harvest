package io.github.jamalam360.rightclickharvest.forge;

import io.github.jamalam360.rightclickharvest.RightClickHarvest;
import io.github.jamalam360.rightclickharvest.RightClickHarvestClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(RightClickHarvest.MOD_ID)
public class RightClickHarvestForge {
	public RightClickHarvestForge() {
		RightClickHarvest.init();
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> RightClickHarvestClient::init);
	}
}
