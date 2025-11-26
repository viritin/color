# CssColor library for Java

Typing is the Java thing and CSS is the web thing. CSS colors have been adopted by a lot of things. This library provides a way to use CSS colors in Java code. It is a simple library that allows you to create and manipulate CSS colors in Java, for better type safety, developer experience and security.

Inspiration for the project came from a number of [Vaadin Flow](https://vaadin.com/flow) add-ons, for which I have either implemented a bad quality ad-hoc solutions or used raw strings (shame on me!). But I can easily imagine dozens of other use cases for this library. Hope you find it useful!

## Design principles

 * Immutability with records, no need to support legacy Java versions
 * Minimal dependencies and module usage (e.g. no `java.desktop` should be needed)
 * Reasonable validations for the input values
 * toString() method returns a CSS color compatible string
 * CSS variables not in scope (would require some sort of context and complicate the design)
 * TODO calculations for parsing

At least for the initial implementation I didn't pay any attention to performance. Things can probably be done in much more performant manner. I'm of course open for PRs and suggestions, but "API ergnomics" must not be sacrificed for performance!

## Impl. notes

Hsl conversions are interpreted to Java code by ChatGPT based on this baeldung article: https://www.baeldung.com/cs/convert-color-hsl-rgb


## Maven 4 testing notes:

*As this is very simple library, I want to test the Maven 4 with it. 0.0.1 was cut to central with Maven 4.0.0-rc3, but it didn't work perfectly yet (needed manual fiddling to push via central plugin and wrong kind of pom.xml file deployed (4.1.0, consumer pom.xml only somehow separately). Needed to downgrade for now, but development with Maven 4 should work (with some warnings), releases still need version 3. Notes below are outdated.*

Built with Maven 4, but open to downgrade if needed for potential contributor. For usage Maven 4 should not cause surprises, works fine with Maven 3.x and Gradle 🧸

Maven 4 gotcha with release: had to execute execute org.sonatype.central:central-publishing-maven-plugin:0.7.0:publish manually. Didn't work automatically with release plugin.
