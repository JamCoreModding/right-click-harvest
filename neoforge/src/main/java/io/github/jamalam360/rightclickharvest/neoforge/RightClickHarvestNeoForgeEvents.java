package io.github.jamalam360.rightclickharvest.neoforge;

import io.github.jamalam360.rightclickharvest.HarvestContext;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;

public class RightClickHarvestNeoForgeEvents {
	/**
	 * Called after right-click-harvesting.
	 */
	public static class AfterHarvest extends Event {
		private final HarvestContext context;

		private AfterHarvest(HarvestContext context) {
			this.context = context;
		}

		protected static void post(HarvestContext context) {
			AfterHarvest event = new AfterHarvest(context);
			NeoForge.EVENT_BUS.post(event);
		}

		public HarvestContext getContext() {
			return this.context;
		}
	}
}
