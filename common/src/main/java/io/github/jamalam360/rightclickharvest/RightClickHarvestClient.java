package io.github.jamalam360.rightclickharvest;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.jamlib.events.client.ClientPlayLifecycleEvents;

public class RightClickHarvestClient {
	private static boolean serverHasSaidHello = false;
	private static boolean warned = false;
	private static int delay = 100;

	public static void init() {
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, HelloPacket.TYPE, (packet, ctx) -> {
			serverHasSaidHello = true;
			RightClickHarvest.LOGGER.info("Server has said hello! Hi Server :)");
		});

		ClientPlayLifecycleEvents.DISCONNECT.register((client) -> {
			serverHasSaidHello = false;
			warned = false;
			delay = 100;
		});

		ClientTickEvent.CLIENT_LEVEL_POST.register((level) -> {
			if (delay > 0) {
				delay--;
			} else if (delay == 0 && !warned && !serverHasSaidHello && RightClickHarvest.CONFIG.get().showServerWarning) {
				if (NetworkManager.canServerReceive(HelloPacket.TYPE)) {
					RightClickHarvest.LOGGER.warn("Server has RightClickHarvest installed, but the hello packet was not received before timeout");
				} else {
//				Minecraft.getInstance().player.displayClientMessage(
//						Component.translatable(
//								"text.rightclickharvest.install_on_server_warning",
//								Component.translatable("config.rightclickharvest.showServerWarning").withStyle(s -> s.withColor(ChatFormatting.GREEN)),
//								Component.literal("false").withStyle(s -> s.withColor(ChatFormatting.GREEN)
//								)),
//						false
//				);
				}
				warned = true;
			}
		});
	}
}
