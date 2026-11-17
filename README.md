# Gingerbread Grove

A candy-and-cookie dimension for Minecraft. Craft a Snow Globe, use it, and arrive in a frosted
gingerbread forest with candy-cane trees, festive mobs, and gingerbread houses to raid.

| Minecraft | Java | Loaders | Branch |
| --- | --- | --- | --- |
| 1.21.1 | 21 | Fabric, NeoForge | `main` |
| 26.3 | 25 | Fabric, NeoForge | `mc-26.3` (planned) |

## Building

Requires a JDK 21 or newer to run Gradle. The Java 21 toolchain used to compile is downloaded
automatically.

```sh
./gradlew build                 # jars land in fabric/build/libs and neoforge/build/libs
./gradlew :fabric:runClient     # Fabric dev client
./gradlew :neoforge:runClient   # NeoForge dev client
./gradlew :fabric:runServer     # dedicated server test (also :neoforge:runServer)
```

## Layout

```text
common/    blocks, items, entities, worldgen, data shared by both loaders (vanilla code only)
fabric/    Fabric entrypoint and platform glue
neoforge/  NeoForge mod class and platform glue
```

Loader-specific calls live behind interfaces in `common/.../platform/services`, and each loader
provides an implementation via `META-INF/services`.

## License

Code is MIT; textures, models, structures and other assets are All Rights Reserved. See `LICENSE`.
