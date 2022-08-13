## Changelog

This release backports V3.0.0 for 1.19.x to 1.18.x. The changelog from V3.0.0 for 1.19.x can be seen below.

Closed Issues: #21.

[Full Changelog](https://github.com/JamCoreModding/RightClickHarvest/compare/2.2.1+1.18...2.3.0+1.18)

### Changelog from V3.0.0
#### Features
- Add a 'harvest in radius' feature (#20), allowing different tiers of hoes to harvest crops in different radii. This can be disabled.
- Significant code refactor - using events rather than mixins.
- Add information on the tags added by RightClickHarvest to a file, `DEVELOPERS.md`.

#### Fixes

- Make the hunger used by harvesting more forgiving.
- Make `useHunger` on by default.
- Allow harvesting if you have no hunger but are in creative mode.
