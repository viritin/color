package in.virit.perf;

import in.virit.color.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Hand-rolled microbenchmark for {@link Color#parseCssColor(String)} and
 * {@link Color#tryParseCssColor(String)}.
 *
 * <p>Not a unit test — surefire only runs {@code @Test} methods, so this file
 * stays out of the test cycle. Run with:
 * {@code mvn -q test-compile && mvn -q exec:java -Dexec.classpathScope=test -Dexec.mainClass=in.virit.perf.ParsePerformanceBenchmark}
 * or via your IDE.
 *
 * <p>Workload mimics jairosvg's parse-heavy use case: a realistic mix of
 * named, hex, rgb, hsl, and modern-function color strings, repeated many
 * times. The mix is sampled deterministically so successive runs are
 * directly comparable.
 */
public final class ParsePerformanceBenchmark {

    /** Realistic SVG-ish distribution. Heavier on named/hex than on hsl/lab. */
    private static final String[] CORPUS = buildCorpus();

    /** Same shape as {@link #CORPUS} but with ~10% malformed strings mixed in. */
    private static final String[] CORPUS_WITH_MALFORMED = buildCorpusWithMalformed();

    private static final int WARMUP_ITERATIONS = 200_000;
    private static final int MEASURE_ITERATIONS = 2_000_000;
    private static final int ROUNDS = 7;

    public static void main(String[] args) {
        System.out.printf(Locale.US, "JVM: %s %s%n",
                System.getProperty("java.vm.name"),
                System.getProperty("java.version"));
        System.out.printf(Locale.US, "Corpus size: %d strings, %d malformed-mix size%n",
                CORPUS.length, CORPUS_WITH_MALFORMED.length);
        System.out.printf(Locale.US, "Warmup: %d iters; Measure: %d iters x %d rounds%n%n",
                WARMUP_ITERATIONS, MEASURE_ITERATIONS, ROUNDS);

        warmup();

        run("strict parseCssColor       (valid corpus)",
                () -> driveStrict(CORPUS, MEASURE_ITERATIONS));
        run("lenient tryParseCssColor   (valid corpus)",
                () -> driveLenient(CORPUS, MEASURE_ITERATIONS));
        run("lenient tryParseCssColor   (with ~10% malformed)",
                () -> driveLenient(CORPUS_WITH_MALFORMED, MEASURE_ITERATIONS));

        // jairosvg-shaped: it only goes through the library for functional
        // notation (rgb/hsl/hwb/lab/...). Named colors and hex have their own
        // fast paths in Colors.color, so the library never sees them.
        String[] functional = buildFunctionalOnly();
        System.out.printf(Locale.US, "%njairosvg-shaped corpus: %d strings (functional notation only)%n",
                functional.length);
        run("strict parseCssColor       (functional only)",
                () -> driveStrict(functional, MEASURE_ITERATIONS));

        // Hex head-to-head: jairosvg has a hand-rolled fast path for #-strings;
        // measure how far behind the library path is — both as a sanity check
        // for keeping that fast path, and to see if HexColor has obvious slack.
        String[] hexOnly = buildHexOnly();
        System.out.printf(Locale.US, "%nhex-only corpus: %d strings%n", hexOnly.length);
        // Both library paths run all the way to RgbColor — jairosvg's actual
        // usage is parse + toRgbColor (it consumes RGB ints to build AWT colors).
        run("strict parseCssColor.toRgbColor   (hex only — library)",
                () -> driveStrictToRgb(hexOnly, MEASURE_ITERATIONS));
        run("lenient tryParseCssColor.toRgbColor (hex only — library)",
                () -> driveLenientToRgb(hexOnly, MEASURE_ITERATIONS));
        run("hand-rolled fast path           (hex only — jairosvg-shaped)",
                () -> driveHandRolledHex(hexOnly, MEASURE_ITERATIONS));

        // "Everything through the library" — the end-state proposed for jairosvg:
        // drop the inline hex + named fast paths in Colors.color and route ALL
        // colors (named, hex, functional) through tryParseCssColor().toRgbColor().
        // This is the number that decides whether those fast paths can go. Run
        // it on the full mixed corpus and again with ~10% malformed, since real
        // SVG input is untrusted and the lenient failure cost matters here.
        System.out.printf(Locale.US, "%nunified library path (drop jairosvg fast paths): full mixed corpus%n");
        run("lenient tryParseCssColor.toRgbColor (full corpus)",
                () -> driveLenientToRgb(CORPUS, MEASURE_ITERATIONS));
        run("lenient tryParseCssColor.toRgbColor (full corpus, ~10% malformed)",
                () -> driveLenientToRgb(CORPUS_WITH_MALFORMED, MEASURE_ITERATIONS));

        // Named-color lookup in isolation — named colors are the single most
        // common token in real SVGs, and dropping jairosvg's NAMED_COLORS map
        // means every one goes through NamedColor.of. The mixed-case corpus
        // exercises the toLowerCase retry branch (non-canonical casing), the
        // lowercase corpus the zero-allocation common path.
        String[] namedLower = buildNamedOnly(false);
        String[] namedMixed = buildNamedOnly(true);
        System.out.printf(Locale.US, "%nnamed-color lookup: %d strings%n", namedLower.length);
        run("strict parseCssColor.toRgbColor   (named, canonical lowercase)",
                () -> driveStrictToRgb(namedLower, MEASURE_ITERATIONS));
        run("strict parseCssColor.toRgbColor   (named, mixed case)",
                () -> driveStrictToRgb(namedMixed, MEASURE_ITERATIONS));
    }

    /**
     * Named colors only — the most common token in real SVGs. When
     * {@code mixedCase} is true the strings use non-canonical casing
     * ({@code Red}, {@code CornflowerBlue}) to exercise the {@code toLowerCase}
     * retry branch in {@link in.virit.color.NamedColor#of(String)}.
     */
    private static String[] buildNamedOnly(boolean mixedCase) {
        String[] lower = {
                "red", "blue", "black", "white", "green", "yellow", "gray",
                "orange", "purple", "cornflowerblue", "darkslategray",
                "lightgoldenrodyellow", "transparent", "rebeccapurple",
                "mediumspringgreen", "navy"
        };
        if (!mixedCase) {
            return lower;
        }
        String[] mixed = new String[lower.length];
        for (int i = 0; i < lower.length; i++) {
            String s = lower[i];
            // Capitalise the first char — enough to miss the as-is lookup and
            // force the lowercase retry without depending on Random.
            mixed[i] = Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
        return mixed;
    }

    private static void warmup() {
        long sink = 0;
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            sink += Color.parseCssColor(CORPUS[i % CORPUS.length]).hashCode();
            sink += Color.tryParseCssColor(CORPUS_WITH_MALFORMED[i % CORPUS_WITH_MALFORMED.length])
                    .map(Object::hashCode).orElse(0);
        }
        if (sink == Long.MIN_VALUE) System.out.println("unreachable");
    }

    private static void run(String label, LongSupplier work) {
        long[] timings = new long[ROUNDS];
        for (int r = 0; r < ROUNDS; r++) {
            long t0 = System.nanoTime();
            long sink = work.getAsLong();
            long elapsed = System.nanoTime() - t0;
            timings[r] = elapsed;
            if (sink == Long.MIN_VALUE) System.out.println("unreachable");
        }
        long min = Long.MAX_VALUE, sum = 0;
        for (long t : timings) {
            min = Math.min(min, t);
            sum += t;
        }
        double avgMs = sum / (double) ROUNDS / 1_000_000.0;
        double minMs = min / 1_000_000.0;
        double nsPerOp = min / (double) MEASURE_ITERATIONS;
        System.out.printf(Locale.US,
                "%-50s  min=%7.2f ms  avg=%7.2f ms  best=%6.2f ns/op%n",
                label, minMs, avgMs, nsPerOp);
    }

    private static long driveStrict(String[] corpus, int iterations) {
        long sink = 0;
        int n = corpus.length;
        for (int i = 0; i < iterations; i++) {
            sink += Color.parseCssColor(corpus[i % n]).hashCode();
        }
        return sink;
    }

    private static long driveLenient(String[] corpus, int iterations) {
        long sink = 0;
        int n = corpus.length;
        for (int i = 0; i < iterations; i++) {
            sink += Color.tryParseCssColor(corpus[i % n])
                    .map(Object::hashCode).orElse(0);
        }
        return sink;
    }

    private static long driveStrictToRgb(String[] corpus, int iterations) {
        long sink = 0;
        int n = corpus.length;
        for (int i = 0; i < iterations; i++) {
            sink += Color.parseCssColor(corpus[i % n]).toRgbColor().hashCode();
        }
        return sink;
    }

    private static long driveLenientToRgb(String[] corpus, int iterations) {
        long sink = 0;
        int n = corpus.length;
        for (int i = 0; i < iterations; i++) {
            sink += Color.tryParseCssColor(corpus[i % n])
                    .map(Color::toRgbColor)
                    .map(Object::hashCode).orElse(0);
        }
        return sink;
    }

    /**
     * Mirrors jairosvg's hex fast path in {@code Colors.color()} — direct
     * Integer.parseInt with index ranges, no String allocations beyond the
     * final RgbColor. Used to measure the cost gap vs the library path.
     */
    private static long driveHandRolledHex(String[] corpus, int iterations) {
        long sink = 0;
        int n = corpus.length;
        for (int i = 0; i < iterations; i++) {
            String s = corpus[i % n];
            int len = s.length();
            int r, g, b;
            double a = 1.0;
            if (len == 7) {
                r = Integer.parseInt(s, 1, 3, 16);
                g = Integer.parseInt(s, 3, 5, 16);
                b = Integer.parseInt(s, 5, 7, 16);
            } else if (len == 4) {
                r = Character.digit(s.charAt(1), 16) * 17;
                g = Character.digit(s.charAt(2), 16) * 17;
                b = Character.digit(s.charAt(3), 16) * 17;
            } else if (len == 9) {
                r = Integer.parseInt(s, 1, 3, 16);
                g = Integer.parseInt(s, 3, 5, 16);
                b = Integer.parseInt(s, 5, 7, 16);
                a = Integer.parseInt(s, 7, 9, 16) / 255.0;
            } else {
                continue;
            }
            sink += new in.virit.color.RgbColor(r, g, b, a).hashCode();
        }
        return sink;
    }

    private static String[] buildCorpus() {
        List<String> list = new ArrayList<>();
        // Named colors are common in SVG.
        Collections.addAll(list,
                "red", "blue", "black", "white", "transparent",
                "green", "yellow", "gray", "orange", "purple",
                "cornflowerblue", "darkslategray", "lightgoldenrodyellow");
        // 6-digit hex (extremely common).
        Collections.addAll(list,
                "#ff0000", "#00ff00", "#0000ff", "#FF5733", "#1a2b3c",
                "#abcdef", "#123456", "#deadbe", "#000000", "#ffffff");
        // 3-digit hex.
        Collections.addAll(list,
                "#f00", "#0f0", "#00f", "#abc", "#fff", "#000");
        // 8-digit hex (with alpha).
        Collections.addAll(list, "#ff000080", "#1a2b3c4d");
        // Legacy rgb()/rgba().
        Collections.addAll(list,
                "rgb(255, 0, 0)",
                "rgb(0, 128, 255)",
                "rgba(255, 0, 0, 0.5)",
                "rgba(10, 20, 30, 0.75)");
        // Modern rgb().
        Collections.addAll(list,
                "rgb(255 0 0)",
                "rgb(64 128 255)",
                "rgb(255 0 0 / 0.5)",
                "rgb(255 0 0/50%)");
        // hsl().
        Collections.addAll(list,
                "hsl(0, 100%, 50%)",
                "hsl(120 100% 50%)",
                "hsl(240 100% 25% / 0.5)");
        // CSS Color 4 function notations (less common in SVG but worth covering).
        Collections.addAll(list,
                "hwb(0 0% 0%)",
                "lab(50 20 -30)",
                "oklch(0.7 0.15 200)");
        return list.toArray(new String[0]);
    }

    private static String[] buildHexOnly() {
        return new String[] {
                "#ff0000", "#00ff00", "#0000ff", "#FF5733", "#1a2b3c",
                "#abcdef", "#123456", "#deadbe", "#000000", "#ffffff",
                "#f00", "#0f0", "#00f", "#abc", "#fff", "#000",
                "#ff000080", "#1a2b3c4d"
        };
    }

    /** Only functional notation — what jairosvg actually routes through the library. */
    private static String[] buildFunctionalOnly() {
        return new String[] {
                "rgb(255, 0, 0)",
                "rgb(0, 128, 255)",
                "rgba(255, 0, 0, 0.5)",
                "rgba(10, 20, 30, 0.75)",
                "rgb(255 0 0)",
                "rgb(64 128 255)",
                "rgb(255 0 0 / 0.5)",
                "rgb(255 0 0/50%)",
                "hsl(0, 100%, 50%)",
                "hsl(120 100% 50%)",
                "hsl(240 100% 25% / 0.5)",
                "hwb(0 0% 0%)",
                "lab(50 20 -30)",
                "lch(50 30 120)",
                "oklab(0.5 0.1 -0.1)",
                "oklch(0.7 0.15 200)",
                "color(display-p3 1 0 0)"
        };
    }

    private static String[] buildCorpusWithMalformed() {
        String[] base = buildCorpus();
        List<String> list = new ArrayList<>();
        Collections.addAll(list, base);
        // Roughly 10% malformed entries — the kind of thing jairosvg might
        // encounter in noisy SVG input.
        Collections.addAll(list,
                "#ggg",         // invalid hex chars
                "#12345",       // wrong hex length
                "notacolor",    // unknown name
                "rgb(a b c)",   // unparseable
                "rgb(300, 0, 0)" // out of range
        );
        return list.toArray(new String[0]);
    }

    @FunctionalInterface
    private interface LongSupplier {
        long getAsLong();
    }

    private ParsePerformanceBenchmark() {
    }
}
