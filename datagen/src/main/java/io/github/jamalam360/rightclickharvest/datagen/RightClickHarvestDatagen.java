package io.github.jamalam360.rightclickharvest.datagen;

import io.github.jamalam360.rightclickharvest.RightClickHarvest;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class RightClickHarvestDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		FabricDataGenerator.Pack pack = gen.createPack();
		pack.addProvider(ItemTagGenerator::new);
		pack.addProvider(BlockTagGenerator::new);
	}

	public static class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
		public ItemTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup) {
			String[] lowTierHoes = new String[]{
					"minecraft:iron_hoe",
					"ae2:certus_quartz_hoe",
					"ae2:fluix_hoe",
					"aether:zanite_hoe",
					"azurepaxels:iron_paxel",
					"copperequipment:copper_hoe",
					"copperequipment:waxed_copper_hoe",
					"earlystage:steel_hoe",
					"exlinecopperequipment:copper_hoe",
					"multitool:iron_multitool",
					"mythicmetals:bronze_hoe",
					"mythicmetals:copper_hoe",
					"mythicmetals:steel_hoe",
					"nature_arise:aluminium_hoe",
					"nature_arise:copper_hoe",
					"sassot:copper_hoe",
			};
			String[] midTierHoes = new String[]{
					"minecraft:golden_hoe",
					"minecraft:diamond_hoe",
					"ae2:nether_quartz_hoe",
					"aether:gravitite_hoe",
					"aether:valkyrie_hoe",
					"amethystequipment:amethyst_hoe",
					"azurepaxels:diamond_paxel",
					"azurepaxels:golden_paxel",
					"bedrockplus:impurebedrock_hoe",
					"betternether:cincinnasite_hoe",
					"betternether:cincinnasite_hoe_diamond",
					"betternether:flaming_ruby_hoe",
					"betternether:nether_ruby_hoe",
					"deep_aether:skyjade_hoe",
					"deeperdarker:resonarium_hoe",
					"emeraldequipment:emerald_hoe",
					"endreborn:curious_endorium_hoe",
					"endreborn:endorium_hoe",
					"endreborn:mysterious_endorium_hoe",
					"exotelcraft:opal_hoe",
					"exotelcraft:ruby_hoe",
					"exotelcraft:upgraded_ruby_hoe",
					"generations_core:amethyst_hoe",
					"generations_core:crystal_hoe",
					"generations_core:ruby_hoe",
					"generations_core:sapphire_hoe",
					"lithereal:burning_litherite_hoe",
					"lithereal:frozen_litherite_hoe",
					"lithereal:infused_litherite_hoe",
					"lithereal:litherite_hoe",
					"lithereal:odysium_hoe",
					"lithereal:withering_litherite_hoe",
					"multitool:diamond_multitool",
					"multitool:golden_multitool",
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
					"mythicupgrades:ametrine_hoe",
					"mythicupgrades:aquamarine_hoe",
					"mythicupgrades:jade_hoe",
					"mythicupgrades:peridot_hoe",
					"mythicupgrades:ruby_hoe",
					"mythicupgrades:sapphire_hoe",
					"mythicupgrades:topaz_hoe",
					"obsidianequipment:obsidian_hoe",
					"phantasm:crystalline_hoe",
					"winterly:cryomarble_hoe",
			};
			String[] highTierHoes = new String[]{
					"minecraft:netherite_hoe",
					"advancednetherite:netherite_diamond_hoe",
					"advancednetherite:netherite_emerald_hoe",
					"advancednetherite:netherite_gold_hoe",
					"advancednetherite:netherite_iron_hoe",
					"azurepaxels:netherite_paxel",
					"deep_aether:stratus_hoe",
					"deeperdarker:warden_hoe",
					"dragonloot:dragon_hoe",
					"enderitemod:enderite_hoe",
					"multitool:netherite_multitool",
					"mythicmetals:legendary_banglum_hoe",
					"oreganized:electrum_hoe",
					"wardentools:warden_hoe",
			};

			TagBuilder lowTierHoeTag = this.getOrCreateRawBuilder(RightClickHarvest.LOW_TIER_HOES);
			TagBuilder midTierHoeTag = this.getOrCreateRawBuilder(RightClickHarvest.MID_TIER_HOES);
			TagBuilder highTierHoeTag = this.getOrCreateRawBuilder(RightClickHarvest.HIGH_TIER_HOES);

			for (String hoe : lowTierHoes) {
				lowTierHoeTag.addOptionalElement(new ResourceLocation(hoe));
			}

			for (String hoe : midTierHoes) {
				midTierHoeTag.addOptionalElement(new ResourceLocation(hoe));
			}

			for (String hoe : highTierHoes) {
				highTierHoeTag.addOptionalElement(new ResourceLocation(hoe));
			}
		}
	}

	public static class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
		public BlockTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
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
