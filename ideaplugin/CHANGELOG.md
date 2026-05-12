# Changelog

## [Unreleased]

### Added

- Gutter swatches and color picker support for the CSS Color 4 types: `HwbColor`, `LabColor`, `LchColor`, `OklabColor`, `OklchColor` and `ColorFunction` (with `ColorSpace.SRGB` / `DISPLAY_P3` / `REC2020` / `PROPHOTO_RGB` / `A98_RGB` / `XYZ` / `XYZ_D50` / `XYZ_D65`), plus their `.of("...")` factories and matching `Color.parseCssColor("hwb(...)" / "oklch(...)" / "color(display-p3 ...)" / ...)` strings.
- The plugin now bundles the Viritin Color library and renders swatches via its own math, so plugin and library stay in sync without re-implementing CSS Color 4 conversions inside the plugin.

### Changed

- Bumped the bundled `in.virit:color` from 0.0.4 to 0.0.5 (adds CSS Color 4 types, normalised HSL hue parsing with `turn`/fractional angles, and `IllegalArgumentException` for malformed `rgb()` / `hsl()`).
- Picking a new color on a CSS Color 4 expression (e.g. `new OklchColor(...)`, `HwbColor.of("...")`) rewrites the whole expression as `new RgbColor(r, g, b[, a])` because the IDE picker yields sRGB and round-tripping into Lab/OkLCh/HWB needs gamut mapping the user rarely expects in this flow.

### Fixed

- `LineMarker is supposed to be registered for leaf elements only` performance warnings from `c.i.c.d.LineMarkerInfo` — color recognition now anchors on the `new` keyword leaf, the method-name identifier (`of` / `parseCssColor`), or the constant-name identifier (`RED` in `NamedColor.RED`) instead of the surrounding composite expression.

## [0.2.0] - 2026-04-29

### Added

- Color swatches now appear next to each entry in the `NamedColor.` autocompletion popup, decorating the IDE's native enum completion.

### Changed

- Editing a `NamedColor.X` reference via the color picker now writes `new RgbColor(...)` instead of `HexColor.of("#...")`.
- Picking a transparent color on a 3-arg `new RgbColor(r, g, b)` or `new HslColor(h, s, l)` now upgrades the expression to the 4-arg constructor so alpha lands in the source.

### Fixed

- Consecutive drags in the color picker now apply correctly instead of stalling after the first commit on a `NamedColor.X` swatch or after a 3→4-arg upgrade.
- Picking a `red` lowercase suggestion in `Color red = █` now inserts the canonical `NamedColor.RED` regardless of the typed-prefix case.

## [0.1.2] - 2026-04-29

First public release.

### Added

- Editor gutter swatches for `RgbColor`, `HexColor`, `HslColor`, `Color.parseCssColor` and `NamedColor.X`.
- Click the swatch to open the IDE color picker; the source is rewritten in the original shape.
- Named-color completion with swatch icons in `Color` contexts.

[Unreleased]: https://github.com/viritin/color/compare/0.2.0...HEAD
[0.2.0]: https://github.com/viritin/color/compare/0.1.2...0.2.0
[0.1.2]: https://github.com/viritin/color/commits/0.1.2
