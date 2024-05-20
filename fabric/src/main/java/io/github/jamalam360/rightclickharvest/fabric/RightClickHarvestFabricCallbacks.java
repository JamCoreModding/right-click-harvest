package io.github.jamalam360.rightclickharvest.fabric;

import io.github.jamalam360.rightclickharvest.HarvestContext;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class RightClickHarvestFabricCallbacks {
	public static final Event<AfterHarvestCallback> AFTER_HARVEST = EventFactory.createArrayBacked(AfterHarvestCallback.class, (callbacks -> (ctx) -> {
		for (AfterHarvestCallback callback : callbacks) {
			callback.afterHarvest(ctx);
		}
	}));

	public interface AfterHarvestCallback {
		/**
		 * Called after right-click-harvesting.
		 */
		void afterHarvest(HarvestContext ctx);
	}
}
