# (Css)Color library for Java

Typing is the Java thing and CSS is the web thing. CSS colors have been adopted by a lot of things. This library provides a way to use CSS colors in Java code. It is a simple library that allows you to create and manipulate CSS colors in Java, for better type safety, developer experience and security.

Inspiration for the project came from a number of [Vaadin Flow](https://vaadin.com/flow) add-ons, for which I have either implemented bad-quality ad-hoc solutions or used raw strings (shame on me!). But I can easily imagine dozens of other use cases for this library. Hope you find it useful!

## Install

Requires Java 17 or later.

Maven:

```xml
<dependency>
    <groupId>in.virit</groupId>
    <artifactId>color</artifactId>
    <version>1.1.0</version>
</dependency>
```

Gradle:

```kotlin
implementation("in.virit:color:1.1.0")
```

## Quick examples

```java
import in.virit.color.Color;
import in.virit.color.HexColor;
import in.virit.color.HslColor;
import in.virit.color.LabColor;
import in.virit.color.NamedColor;
import in.virit.color.OklchColor;
import in.virit.color.RgbColor;

// The base type is Color; concrete records implement it.
Color red       = new RgbColor(255, 0, 0);
Color halfRed   = new RgbColor(255, 0, 0, 0.5);
Color tomato    = NamedColor.TOMATO;
Color clear     = NamedColor.TRANSPARENT;        // rgba(0, 0, 0, 0)
Color brand     = HexColor.of("#82CB32");
Color shortHex  = HexColor.of("#F0A");           // #FF00AA
Color hsl       = new HslColor(120, 100, 50);

// CSS Color 4 spaces are first-class too.
Color lab    = LabColor.of("lab(50 20 -30)");
Color oklch  = OklchColor.of("oklch(0.7 0.15 200)");

// Parse anything CSS-shaped (named, hex, rgb/rgba, hsl/hsla, hwb,
// lab, lch, oklab, oklch, color(<space> ...)).
Color parsed = Color.parseCssColor("rgb(0 200 80 / 0.5)");

// Lenient counterpart for untrusted input (e.g. SVG attribute values):
// returns Optional.empty() on null or any malformed string instead of
// throwing — caller picks the fallback.
Color fallback = Color.tryParseCssColor("#ggg")
        .orElse(NamedColor.BLACK);

// toString() always returns a CSS-compatible string.
String css    = brand.toString();                // "#82CB32"
String rgbCss = halfRed.toString();              // "rgb(255 0 0 / 0.50)"

// Convert between formats.
RgbColor asRgb = brand.toRgbColor();
HslColor asHsl = asRgb.toHslColor();
HexColor asHex = asRgb.toHexColor();

// HSL has handy color-math helpers.
HslColor darker  = asHsl.darken(20);             // 20 lightness points darker
HslColor lighter = asHsl.lighten(0.2);           // 20% relatively lighter
HslColor sat     = asHsl.saturate(10);
HslColor opp     = asHsl.complement();           // hue + 180°
```

## CSS coverage

`Color.parseCssColor` (and the per-type `of(...)` methods) handle the
shapes the CSS Color Module Level 4 spec actually puts into stylesheets:

- `#hex` — 3, 6 and 8 digit forms
- CSS named colors (147 of them, plus `transparent`)
- `rgb()` / `rgba()` — legacy comma and modern space/slash forms
- `hsl()` / `hsla()` — legacy comma and modern space/slash forms
- `hwb()`
- `lab()` and `lch()`
- `oklab()` and `oklch()`
- `color(<space> r g b [/ a])` — predefined spaces (`srgb`,
  `display-p3`, `a98-rgb`, `rec2020`, `prophoto-rgb`, etc.)

Not yet supported, by design or by deferral:

- CSS variables (`var(--name)`) — would require a resolution context.
- `none` keyword for missing components.
- `color-mix()` and relative color syntax.
- `calc(...)` inside component values.

## IDE support

The plugins are optional but shorten the feedback loop when working with colors.

- **IntelliJ IDEA** — *Viritin Color* on the [JetBrains Marketplace](https://plugins.jetbrains.com/search?search=Viritin+Color). Renders gutter swatches next to `RgbColor`, `HexColor`, `HslColor`, `Color.parseCssColor` and `NamedColor.X` expressions; clicking the swatch opens the IDE color picker and rewrites the source in the original shape. Named-color completion is decorated with swatches in `Color`-expected slots.
- **NetBeans** — install [junichi11's *Color Codes Preview*](https://plugins.netbeans.apache.org/catalogue/?id=24) plus the *Viritin Color* extension (NBM in [`nbplugin/`](nbplugin/)). Same swatches; line-based recognizer (no completion popup, since the SPI doesn't expose that).

## Design principles

- Immutability with records, no need to support legacy Java versions.
- Minimal dependencies and module usage (e.g. no `java.desktop` should be needed).
- Reasonable validations for the input values.
- `toString()` returns a CSS-compatible string.
- API ergonomics first; performance is shaped around that rather than the other way around.

`parseCssColor` is now fast enough to be used freely in hot paths (~25
ns/op average across a mixed corpus on a modern desktop), with the
functional-notation case at ~60 ns/op. PRs that improve this further
without compromising the public API are welcome.

## Impl. notes

HSL conversions are interpreted into Java code by ChatGPT based on this Baeldung article: https://www.baeldung.com/cs/convert-color-hsl-rgb
