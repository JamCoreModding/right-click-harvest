package io.github.jamalam360.rightclickharvest;

import io.github.jamalam360.jamlib.config.ConfigExtensions;
import net.minecraft.network.chat.Component;

import java.util.List;

public class Config implements ConfigExtensions<Config> {
	public boolean requireHoe = true;
	public boolean harvestInRadius = true;
	public HungerLevel hungerLevel = HungerLevel.NORMAL;

	@Override
	public List<Link> getLinks() {
		return List.of(
				new Link(Link.DISCORD, "https://jamalam.tech/Discord", Component.translatable("config.rightclickharvest.discord")),
				new Link(Link.GITHUB, "https://github.com/JamCoreModding/quicker-connect-button", Component.translatable("config.rightclickharvest.github")),
				new Link(Link.GENERIC_LINK, "https://modrinth.com/mod/quicker-connect-button", Component.translatable("config.rightclickharvest.modrinth"))
		);
	}

	public enum HungerLevel {
		NONE(0.0f),
		LOW(0.5f),
		NORMAL(1.0f),
		HIGH(2.0f);

		public final float modifier;

		HungerLevel(float modifier) {
			this.modifier = modifier;
		}
	}
}
