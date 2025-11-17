package io.github.jamalam360.rightclickharvest;

import blue.endless.jankson.Comment;
import io.github.jamalam360.jamlib.config.ConfigExtensions;
import io.github.jamalam360.jamlib.config.HiddenInGui;
import java.util.List;
import net.minecraft.network.chat.Component;

public class Config implements ConfigExtensions<Config> {

    public boolean requireHoe = false;
    public boolean harvestInRadius = true;
    public HungerLevel hungerLevel = HungerLevel.NONE;
    public ExperienceType experienceType = ExperienceType.NONE;
    public boolean showServerWarning = true;
    @Comment("Modpack developers, set this to true to stop RCH telling users that they probably need to equip a hoe to harvest crops (if requireHoe is set to true). This message will only be displayed once.")
    @HiddenInGui
    public boolean hasUserBeenWarnedForNotUsingHoe = false;
    public boolean enablePermissions = false;

    @Override
    public List<Link> getLinks() {
        return List.of(
              new Link(Link.DISCORD, "https://jamalam.tech/discord", Component.translatable("config.rightclickharvest.discord")),
              new Link(Link.GITHUB, "https://github.com/JamCoreModding/right-click-harvest", Component.translatable("config.rightclickharvest.github")),
              new Link(Link.GENERIC_LINK, "https://modrinth.com/mod/rightclickharvest", Component.translatable("config.rightclickharvest.modrinth"))
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
    
    public enum ExperienceType {
        NONE,
        COST,
        REWARD
    }
}
