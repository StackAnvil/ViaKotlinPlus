# ViaKotlinPlus

ViaKotlinPlus is a Kotlin rewrite of [ViaFabricPlus](https://github.com/ViaVersion/ViaFabricPlus) 5.1.4. Every source file under `src/` and `viafabricplus-api/src/` is Kotlin; no `.java` sources remain.

The mod keeps ViaFabricPlus' purpose: a Fabric client mod that lets a modern Minecraft client join servers running older Minecraft versions through ViaVersion, ViaBackwards, ViaLegacy and ViaAprilFools.

## Requirements

- Java 25
- A Minecraft 26.3 Fabric development environment (the Gradle wrapper and Loom handle this)

## Build

```bash
./gradlew build
```

The built mod is in `build/libs/`.

## Run

```bash
./gradlew runClient
```

## Verified

The rewrite was built and launched against a locally running offline Spigot 1.8.8 server. A client joined the server, received the world and stayed in game.

For repeatable testing, the JVM property `viakotlinplus.test.targetVersion` forces the target protocol version before connecting:

```bash
JAVA_TOOL_OPTIONS="-Dviakotlinplus.test.targetVersion=1.8" \
  ./gradlew runClient --args="--quickPlayMultiplayer 127.0.0.1:25565"
```

## Notes on the Mixin port

SpongePowered Mixin requires injector methods to live on the mixin class itself and, for static targets, to be static. Kotlin can only express static members through a companion object, and Mixin rejects the `Companion` field. The build therefore post-processes the compiled mixin classes: companion bodies are moved onto the mixin class, and a private static bridge with the original signature and Mixin annotations forwards into the body. Static fields are made private, shadow field initialisers are removed, and Kotlin's constructor NOPs are stripped, all of which Mixin requires.

The transformation lives in `build.gradle.kts` and runs after `compileKotlin`. All mixins are enabled in `viafabricplus.mixins.json`.

## License

GPL-3.0, inherited from ViaFabricPlus. See `LICENSE`.
