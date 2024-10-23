package io.github.jamalam360.rightclickharvest.datagen;

import io.github.jamalam360.rightclickharvest.RightClickHarvest;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class RightClickHarvestDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		FabricDataGenerator.Pack pack = gen.createPack();
		pack.addProvider(ItemTagGenerator::new);
		pack.addProvider(BlockTagGenerator::new);
	}

	public static class ItemTagGenerator extends FabricTagProvider<Item> {
		public ItemTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, Registries.ITEM, registriesFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup) {
			String[] lowTierHoes = new String[]{
					"minecraft:iron_hoe",
					"ae2:certus_quartz_hoe",
					"ae2:fluix_hoe",
					"exlinecopperequipment:copper_hoe",
					"mythicmetals:bronze_hoe",
					"mythicmetals:copper_hoe",
					"mythicmetals:steel_hoe",
					"azurepaxels:iron_paxel",
					"earlystage:steel_hoe",
					"sassot:copper_hoe",
					"nature_arise:aluminium_hoe",
					"nature_arise:copper_hoe"
			};
			String[] midTierHoes = new String[]{
					"minecraft:golden_hoe",
					"minecraft:diamond_hoe",
					"ae2:nether_quartz_hoe",
					"emeraldequipment:emerald_hoe",
					"mythicmetals:adamantite_hoe",
					"mythicmetals:aquarium_hoe",
					"mythicmetals:banglum_hoe",
					"mythicmetals:carmot_hoe",
					"mythicmetals:celestium_hoe",
					"mythicmetals:durasteel_hoe",
					"mythicmetals:hallowed_hoe",
					"mythicmetals:kyber_hoe",
					"mythicmetals:metallurgium_hoe",
					"mythicmetals:mythril_hoe",
					"mythicmetals:orichalcum_hoe",
					"mythicmetals:osmium_hoe",
					"mythicmetals:palladium_hoe",
					"mythicmetals:prometheum_hoe",
					"mythicmetals:quadrillum_hoe",
					"mythicmetals:runite_hoe",
					"mythicmetals:star_platinum_hoe",
					"mythicmetals:stormyx_hoe",
					"mythicmetals:tidesinger_hoe",
					"deeperdarker:resonarium_hoe",
					"azurepaxels:golden_paxel",
					"azurepaxels:diamond_paxel",
					"obsidianequipment:obsidian_hoe",
					"phantasm:crystalline_hoe",
					"generations_core:amethyst_hoe",
					"generations_core:crystal_hoe",
					"generations_core:ruby_hoe",
					"generations_core:sapphire_hoe",
					"amethystequipment:amethyst_hoe",
					"mythicupgrades:ametrine_hoe",
					"mythicupgrades:aquamarine_hoe",
					"mythicupgrades:jade_hoe",
					"mythicupgrades:peridot_hoe",
					"mythicupgrades:ruby_hoe",
					"mythicupgrades:sapphire_hoe",
					"mythicupgrades:topaz_hoe",
					"lithereal:burning_litherite_hoe",
					"lithereal:frozen_litherite_hoe",
					"lithereal:infused_litherite_hoe",
					"lithereal:litherite_hoe",
					"lithereal:odysium_hoe",
					"lithereal:withering_litherite_hoe",
					"exotelcraft:opal_hoe",
					"exotelcraft:ruby_hoe",
					"exotelcraft:upgraded_ruby_hoe",
					"betternether:cincinnasite_hoe",
					"betternether:cincinnasite_hoe_diamond",
					"betternether:flaming_ruby_hoe",
					"betternether:nether_ruby_hoe",
					"bedrockplus:impurebedrock_hoe",
					"endreborn:endorium_hoe",
					"endreborn:curious_endorium_hoe",
					"endreborn:mysterious_endorium_hoe",
					"winterly:cryomarble_hoe"
			};
			String[] highTierHoes = new String[]{
					"minecraft:netherite_hoe",
					"mythicmetals:legendary_banglum_hoe",
					"deeperdarker:warden_hoe",
					"azurepaxels:netherite_paxel",
					"advancednetherite:netherite_iron_hoe",
					"advancednetherite:netherite_gold_hoe",
					"advancednetherite:netherite_emerald_hoe",
					"advancednetherite:netherite_diamond_hoe",
					"wardentools:warden_hoe",
					"dragonloot:dragon_hoe",
					"enderitemod:enderite_hoe"
			};

			for (String hoe : lowTierHoes) {
				this.getOrCreateTagBuilder(RightClickHarvest.LOW_TIER_HOES).setReplace(false).addOptional(ResourceLocation.parse(hoe));
			}

			for (String hoe : midTierHoes) {
				this.getOrCreateTagBuilder(RightClickHarvest.MID_TIER_HOES).setReplace(false).addOptional(ResourceLocation.parse(hoe));
			}

			for (String hoe : highTierHoes) {
				this.getOrCreateTagBuilder(RightClickHarvest.HIGH_TIER_HOES).setReplace(false).addOptional(ResourceLocation.parse(hoe));
			}
		}
	}

	public static class BlockTagGenerator extends FabricTagProvider<Block> {
		public BlockTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, Registries.BLOCK, registriesFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup) {
			this.getOrCreateTagBuilder(RightClickHarvest.BLACKLIST)
					.setReplace(false);
			this.getOrCreateTagBuilder(RightClickHarvest.HOE_NEVER_REQUIRED)
					.setReplace(false)
					.add(Blocks.COCOA);
			this.getOrCreateTagBuilder(RightClickHarvest.RADIUS_HARVEST_BLACKLIST)
					.setReplace(false)
					.add(Blocks.COCOA)
					.add(Blocks.SUGAR_CANE)
					.add(Blocks.CACTUS);
		}
	}
}
