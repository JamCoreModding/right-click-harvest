package io.github.jamalam360.rightclickharvest.mixin;

import io.github.jamalam360.rightclickharvest.mixinsupport.ServerPlayerLanguageAccessor;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements ServerPlayerLanguageAccessor {
	@Unique
	private String rightclickharvest$language = "en_us";

	@Inject(
			method = "updateOptions",
			at = @At("TAIL")
	)
	private void rightclickharvest$captureLanguage(ServerboundClientInformationPacket packet, CallbackInfo ci) {
		this.rightclickharvest$language = packet.language();
	}

	@Override
	public String rightclickharvest$getLanguage() {
		return this.rightclickharvest$language;
	}
}
