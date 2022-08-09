## Changelog

### Features

- Build against 1.19.2.
- Add a 'harvest in radius' feature (#20), allowing different tiers of hoes to harvest crops in different radii. This can be disabled.
- Significant code refactor - using events rather than mixins.
- Add information on the tags added by RightClickHarvest to a file, `DEVELOPERS.md`.

### Fixes

- Make the hunger used by harvesting more forgiving.
- Make `useHunger` on by default.
- Allow harvesting if you have no hunger but are in creative mode.

Closed Issues: #20.

[Full Changelog](https://github.com/JamCoreModding/RightClickHarvest/compare/2.2.3...3.0.0)
