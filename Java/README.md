# Gilded Tros Refactoring Kata (Java)

The instructions are in [GildedTrosRequirements.txt](GildedTrosRequirements.txt).

## Getting the code

The repository is public on GitHub at [roomsg/gilded-tros](https://github.com/roomsg/gilded-tros).
Work happens on `main`.

```bash
git clone https://github.com/roomsg/gilded-tros.git
cd gilded-tros/Java
```

With an SSH key set up on GitHub, you can clone over SSH instead: `git clone git@github.com:roomsg/gilded-tros.git`.

The repository holds the kata in several languages; this `Java/` folder is self-contained.
It started as a copy of the Axxes Gilded Tros kata, which is no longer tracked as a remote,
so `origin` is the GitHub repository and the only one to push to.

## Requirements

| | Java version |
|---|---|
| JDK to run the build | **17 or newer** (tested with 21) |
| Bytecode produced | Java 8 (runs on any JVM 8+) |

The build uses Gradle 9.9.1 through the wrapper, and Gradle 9 itself requires JVM 17 or newer to run.
On an older JDK, `./gradlew` stops with `Gradle requires JVM 17 or later to run`.
Make sure `JAVA_HOME` (and, in IntelliJ, *Settings → Build Tools → Gradle → Gradle JVM*) points to JDK 17+.

The code is still compiled with `--release 8`, so it targets the Java 8 language level and API,
whichever JDK runs the build.

## Build and test

```bash
./gradlew test
./gradlew test --tests com.gildedtros.GildedTrosTest   # a single test class
```

## Assessment Feedback

### Remarks

- The original implementation for the "Good Wine" increases the quality, as specified, yet increases the quality twice as fast __once expired__. I kept that logic for now, not knowing if it is a bug or a desired behavior.

### Assumptions

- Java devs@axxes are all on (at least) JDK17 (release 2021).
- The requirements were not specifying which conferences are considered 'very interesting'. The original implementation only considered 'Re:Factor' and 'HAXX' as very interesting, I kept that same behavior.

### Extra

- Since input data can't always be trusted, `Updaters` calls a `Sanitizer` hook just before and just
  after each item's update. `GildedTros` uses `Sanitizers.LOG`, which reports anomalies (a null item
  name, or a quality outside the item's allowed range) to `System.out` without changing anything.
  The same hook could later be used to actually correct anomalous values.
