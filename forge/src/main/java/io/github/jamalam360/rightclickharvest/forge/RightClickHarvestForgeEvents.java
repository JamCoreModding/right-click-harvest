package io.github.jamalam360.rightclickharvest.forge;

import io.github.jamalam360.rightclickharvest.HarvestContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public class RightClickHarvestForgeEvents {
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
			MinecraftForge.EVENT_BUS.post(event);
		}

		public HarvestContext getContext() {
			return this.context;
		}
	}
}
