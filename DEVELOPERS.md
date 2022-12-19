# Developers

This file has some information on adding support for RightClickHarvest to your own mod.

## Block Tags

- `hoe_requred`: crops/blocks that require a hoe to be harvested, if `Config.requireHoe` is true.
- `radius_harvest_blacklist`: crops/blocks that are ignored by the harvest in radius feature (e.g.
  cocoa beans).

## Item Tags

These tags are used to determine the radius when harvesting in a radius.

- `low_tier_hoes`: hoes that are equivalent to iron hoes.
- `mid_tier_hoes`: hoes that are equivalent to diamond and gold hoes.
- `high_tier_hoes`: hoes that are equivalent to netherite hoes.

## API

RightClickHarvest has a minimal API that I use for adding compatibility with other mods. It can be
used by depending on RCH via Maven (below). It currently only provides the callbacks accessible
via `RightClickHarvestCallbacks`.

## Maven

RightClickHarvest is available on [my maven](https://maven.jamalam.tech). You can check versions by
browsing the maven.

```groovy
repositories {
    maven {
        url "https://maven.jamalam.tech/"
    }
}

dependencies {
    modImplementation("io.github.jamalam360:right-click-harvest:VERSION")
}
```
