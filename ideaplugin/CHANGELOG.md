# Changelog

## [Unreleased]

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
