package io.github.jamalam360.rightclickharvest;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class HelloPacket {
	public static final ResourceLocation TYPE = RightClickHarvest.id("hello");

	public static FriendlyByteBuf of() {
		return new FriendlyByteBuf(Unpooled.buffer());
	}
}
