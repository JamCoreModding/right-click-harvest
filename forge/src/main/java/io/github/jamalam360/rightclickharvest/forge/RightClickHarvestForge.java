package io.github.jamalam360.rightclickharvest.forge;

import dev.architectury.platform.forge.EventBuses;
import io.github.jamalam360.rightclickharvest.RightClickHarvest;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Random;

@Mod(RightClickHarvest.MOD_ID)
public class RightClickHarvestForge {
	public RightClickHarvestForge() {
		EventBuses.registerModEventBus(RightClickHarvest.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
		RightClickHarvest.init();
	}
}
