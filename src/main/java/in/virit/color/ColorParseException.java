package in.virit.color;

/**
 * Thrown when a CSS color string cannot be parsed.
 *
 * <p>This is an {@link IllegalArgumentException}, so the documented contract of
 * {@link Color#parseCssColor(String)} (which throws {@code IllegalArgumentException}
 * on malformed input) is unchanged — existing {@code catch} clauses keep working.
 *
 * <p>The difference is that it does <em>not</em> capture a stack trace. Parse
 * failures are an expected, recoverable outcome on the lenient path
 * ({@link Color#tryParseCssColor(String)}), which is used to consume untrusted
 * SVG/CSS where malformed values are routine. Filling in a stack trace is the
 * dominant cost of constructing an exception, and on a malformed-heavy workload
 * it dwarfs the actual parsing work. Suppressing it makes the lenient path's
 * failure case cheap without changing any observable behaviour beyond an empty
 * stack trace on the (normally swallowed) exception.
 */
final class ColorParseException extends IllegalArgumentException {

    ColorParseException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        // Parse failures are control flow on the lenient path, not bugs — skip
        // the expensive stack walk.
        return this;
    }
}
