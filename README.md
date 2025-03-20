![RCH Banner](https://www.bisecthosting.com/images/CF/RightClickHarvest/BH_RH_HEADER.webp)

<center><img width="100%" src="https://github.com/JamCoreModding/right-click-harvest/blob/main/demo.gif?raw=true" /></center>
<h3 align="center">RightClickHarvest is a mod for Fabric, Forge, and NeoForge that allows harvesting crops and plants with just a right click.</h3>

- Right click to harvest crops, replanting the seeds automatically.
- Harvest all vanilla crops, including cocoa beans, cactus, sugar cane, and more.
- Enchant your hoe with fortune to get a higher yield.
- Harvest 99.9% of modded crops without extra configuration.
- Configure if a hoe is required and whether harvesting uses hunger points.
- Configure whether higher tier hoes harvest in a larger radius.

<h2 align="center">Config</h2>

- **Require Hoe**: whether a hoe is required to harvest crops (_default: false_).
- **Harvest in Radius**: whether to harvest crops in a bigger radius as the 'tier' of hoe used increases (_default: true_).
- **Hunger Level**: the amount of hunger to require when harvesting, or none (_default: none_).
- **Experience Type**: whether to consume or reward XP points for harvesting (_default: none_).
- **Show Server Warning**: whether to display a warning when connecting to a server which does not have RightClickHarvest installed, as the mod will not work (_default: true_).

There are also a number of block and item tags available to customise the mod's behaviour, documented [here](https://docs.jamalam.tech/right-click-harvest/tags/).

<h2 align="center">FAQ</h2>

- **Does this work client-side?** No, RightClickHarvest **must** be installed on the server and can **optionally** be installed on the client.
- **How do I configure the mod?** On Fabric, make sure ModMenu is installed. From there, you can simply access the config through the mods menu on both platforms. For servers, edit `config/rightclickharvest.json5`.
- **The mod isn't working.** The most common issue is that **Require Hoe** is set to `true` in the config, meaning that you have to be holding a hoe to harvest crops.
- **Can you update to [version]?** The versions I currently support for all of my mods are documented [here](https://docs.jamalam.tech/supported-versions/). If the version you want is not listed, the answer is probably no.
- **How do I make the mod work with crops from Supplementaries?** A compatibility mod is available: [Curseforge](https://www.curseforge.com/minecraft/mc-mods/rightclickharvest-supplementaries-compat), [Modrinth](https://modrinth.com/mod/rch-supplementaries-compat)
- **What are the dependencies of this mod?** Architectury API and JamLib are required, as well as Fabric API on Fabric.

<h3 align="center">For any questions not listed above, please join the Discord by clicking on the banner below.</h3>

<h3 align="center">Please report any bugs to the <a href="https://github.com/JamCoreModding/right-click-harvest">GitHub page</a>.</h3>

<a href="https://discord.jamalam.tech"><img alt="Join the Discord" src="https://www.bisecthosting.com/images/CF/RightClickHarvest/BH_RH_BANNER3.webp" /></a>

<a href="https://bisecthosting.com/jamalam"><img alt="Rent a Server (25% off with code JAMALAM)" src="https://www.bisecthosting.com/images/CF/RightClickHarvest/BH_RH_PROMO.webp" /></a>
