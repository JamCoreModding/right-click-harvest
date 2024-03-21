package io.github.jamalam360.rightclickharvest.forge;

import dev.architectury.platform.forge.EventBuses;
import io.github.jamalam360.rightclickharvest.RightClickHarvest;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RightClickHarvest.MOD_ID)​​
​​public class RightClickHarvestForge {
​	pub​lic RightClickHarvestForge() {​
​		Eve​ntBuses.regis​terModEventBus(RightCli​ckHarv​est.MOD_ID, FMLJa​vaModLo​adingContext.get().getModEv​entBus());
		Right​ClickHarvest.init();
	}​​
}​​
​​​​