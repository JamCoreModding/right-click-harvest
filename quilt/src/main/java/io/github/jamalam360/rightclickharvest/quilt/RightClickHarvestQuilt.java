package io.github.jamalam360.rightclickharvest.quilt;

import io.github.jamalam360.rightclickharvest.fabriclike.RightClickHarvestFabricLike;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class RightClickHarvestQuilt implements ModInitializer {
	@Override
	public void onInitialize(ModContainer mod) {
		RightClickHarvestFabricLike.init();
	}
}
