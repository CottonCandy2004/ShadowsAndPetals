# Repository Guidelines

## Project Structure & Module Organization

This is a single-module Java 21 NeoForge mod for Minecraft 1.21.1. Production code lives under `src/main/java/com/sshakusora/shadowsandpetals`; keep features grouped by domain, such as `block`, `item`, `worldgen`, `event`, or `compat`. Hand-authored assets, data, mixin configuration, and structures belong in `src/main/resources`. Data generators write to `src/generated/resources`, which is included in the main resource set. Treat `build/`, `run/`, `logs/`, and `.gradle/` as local output, not source.

## Build and Development Commands

Use the checked-in Gradle wrapper (replace `./gradlew` with `.\gradlew.bat` on Windows):

- `./gradlew build` — compiles Java and creates the mod JAR in `build/libs`.
- `./gradlew runClient` — launches a development client and installs configured shader packs.
- `./gradlew runServer` — launches the NeoForge development server.
- `./gradlew runData` — regenerates resources in `src/generated/resources`; review generated diffs before committing.
- `./gradlew runStructureEditor` — prepares and opens the dedicated structure-editing world.

## Coding Style & Naming Conventions

Use four-space indentation, braces on the same line, and standard Java conventions: `PascalCase` types, `camelCase` methods and fields, and `UPPER_SNAKE_CASE` constants. Keep packages lowercase beneath `com.sshakusora.shadowsandpetals`. Use descriptive registry classes such as `ItemRegistry` and namespace resource identifiers through `ShadowsAndPetals.asResource(...)`. No formatter or linter is configured; match nearby code and keep imports explicit and organized.

## Commit & Pull Request Guidelines

Recent history uses short, imperative Conventional Commit-style subjects such as `feat: add create compat`, `fix: fix recessed lamp rendering`, and `refactor: extract ...`. Keep each commit scoped to one concern. Pull requests should explain the change, testing performed, compatibility impact, and any generated-resource updates. Link relevant issues and include screenshots or video for visual, UI, model, texture, or rendering changes. Ensure `./gradlew build` passes before requesting review.
