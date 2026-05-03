# CssColor library for Java

Typing is the Java thing and CSS is the web thing. CSS colors have been adopted by a lot of things. This library provides a way to use CSS colors in Java code. It is a simple library that allows you to create and manipulate CSS colors in Java, for better type safety, developer experience and security.

Inspiration for the project came from a number of [Vaadin Flow](https://vaadin.com/flow) add-ons, for which I have either implemented bad-quality ad-hoc solutions or used raw strings (shame on me!). But I can easily imagine dozens of other use cases for this library. Hope you find it useful!

## Install

Maven:

```xml
<dependency>
    <groupId>in.virit</groupId>
    <artifactId>color</artifactId>
    <version>0.0.4</version>
</dependency>
```

Gradle:

```kotlin
implementation("in.virit:color:0.0.4")
```

## Quick examples

```java
import in.virit.color.Color;
import in.virit.color.HexColor;
import in.virit.color.HslColor;
import in.virit.color.NamedColor;
import in.virit.color.RgbColor;

// The base type is Color; concrete records implement it.
Color red       = new RgbColor(255, 0, 0);
Color halfRed   = new RgbColor(255, 0, 0, 0.5);
Color tomato    = NamedColor.TOMATO;
Color brand     = HexColor.of("#82CB32");
Color shortHex  = HexColor.of("#F0A");           // #FF00AA
Color hsl       = new HslColor(120, 100, 50);

// Parse anything CSS-shaped (named, hex, rgb(...), hsl(...)).
Color parsed = Color.parseCssColor("rgb(0 200 80 / 0.5)");

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

## IDE support

The plugins are optional but shorten the feedback loop when working with colors.

- **IntelliJ IDEA** — *Viritin Color* on the [JetBrains Marketplace](https://plugins.jetbrains.com/search?search=Viritin+Color). Renders gutter swatches next to `RgbColor`, `HexColor`, `HslColor`, `Color.parseCssColor` and `NamedColor.X` expressions; clicking the swatch opens the IDE color picker and rewrites the source in the original shape. Named-color completion is decorated with swatches in `Color`-expected slots.
- **NetBeans** — install [junichi11's *Color Codes Preview*](https://plugins.netbeans.apache.org/catalogue/?id=24) plus the *Viritin Color* extension (NBM in [`nbplugin/`](nbplugin/)). Same swatches; line-based recognizer (no completion popup, since the SPI doesn't expose that).

## Design principles

- Immutability with records, no need to support legacy Java versions.
- Minimal dependencies and module usage (e.g. no `java.desktop` should be needed).
- Reasonable validations for the input values.
- `toString()` returns a CSS-compatible string.
- CSS variables are out of scope (would require some sort of context and complicate the design).
- TODO: `calc(...)` parsing.

At least for the initial implementation I didn't pay any attention to performance. Things can probably be done in a much more performant manner. I'm of course open for PRs and suggestions, but "API ergonomics" must not be sacrificed for performance!

## Impl. notes

HSL conversions are interpreted into Java code by ChatGPT based on this Baeldung article: https://www.baeldung.com/cs/convert-color-hsl-rgb
