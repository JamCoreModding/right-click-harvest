package io.github.jamalam360.rightclickharvest;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record HelloPacket() implements CustomPacketPayload {
	public static final Type<HelloPacket> TYPE = new CustomPacketPayload.Type<>(RightClickHarvest.id("hello"));
	public static final StreamCodec<RegistryFriendlyByteBuf, HelloPacket> STREAM_CODEC = StreamCodec.of((buf, obj) -> {
	}, (buf) -> new HelloPacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
