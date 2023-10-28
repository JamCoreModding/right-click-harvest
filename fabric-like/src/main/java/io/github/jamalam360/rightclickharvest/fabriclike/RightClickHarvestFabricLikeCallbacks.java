package io.github.jamalam360.rightclickharvest.fabriclike;

import io.github.jamalam360.rightclickharvest.HarvestContext;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class RightClickHarvestFabricLikeCallbacks {
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
