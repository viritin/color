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
