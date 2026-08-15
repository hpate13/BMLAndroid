# BML — Bloons TD 6 Mod Loader for Android (no root)

A MelonLoader-style mod loader for **Bloons TD 6** that runs on Android **without root** by repackaging the game, so it can run the BTD6 mods the community distributes on GitHub.

> **Status: scaffold with the repackaging pipeline implemented.** The runtime loader payload (LemonLoader's binaries) is not bundled yet — see [What's next](#whats-next).

---

## What this is

BML is an Android app that turns your installed copy of Bloons TD 6 into a moddable one, then manages the mods:

- **Extracts** your installed BTD6 APK
- **Injects** the LemonLoader payload
- **Re-signs** the APK with a locally generated key
- **Installs** the repacked copy (after uninstalling the original)
- **Stages** mods in a public folder and **launches** the game

It is deliberately scoped to BTD6: a curated GitHub mod catalog, one-tap install/remove, and the repack + launch flow.

## Why repackaging instead of root

Android blocks apps from writing into another app's private data and from injecting code into another process. MelonLoader's PC approach (drop files into the game folder) therefore doesn't translate directly.

Two ways around it:

- **Root** — read/write `/data/data/...` and `ptrace` the game process.
- **Repackaging (this project)** — bake the loader into the APK itself, re-sign it, and install the modified copy. No root required.

Repackaging has tradeoffs: the repacked game can't update through Google Play (you re-patch each update), it fails Play Integrity checks, and using any mods violates Ninja Kiwi's ToS.

## Relationship to MelonLoader and LemonLoader

- **[MelonLoader](https://github.com/LavaGang/MelonLoader)** — the universal Unity mod loader the BTD6 PC community standardizes on. Its Android support is still work-in-progress.
- **[LemonLoader](https://github.com/LemonLoader)** — the Android port of MelonLoader, exposing the same `MelonMod`/Harmony/Il2CppInterop mod API.
- **BTD6** is an IL2CPP Unity game on both PC and Android. Mods are C# `MelonMod` assemblies, mostly built on **[BTD Mod Helper](https://github.com/gurrenm3/BTD-Mod-Helper)**, distributed as release DLLs on GitHub.

BML does **not** reimplement IL2CPP hooking. It reuses LemonLoader's payload and adds the BTD6-specific pieces: repackaging, mod catalog, install/remove, and launching.

## Architecture

```
┌──────────────────────────── BML app (Kotlin / Jetpack Compose) ───────────────────────────┐
│                                                                                            │
│  Repackager ──► ApkExtractor ──► ApkPatcher ──► KeystoreManager ──► ApkSigner             │
│                    (pull APK)     (inject lib)   (local key)       (v1/v2/v3)              │
│       └──► PackageInstallerHelper (uninstall original, install repacked)                   │
│                                                                                            │
│  ModCatalog (GitHub) ──► ModInstaller (public /BML/Mods dir) ──► LoaderCore (launch)       │
└─────────────────────────────────────────────┬──────────────────────────────────────────────┘
                                              │ installs & launches
                                com.ninjakiwi.bloonstd6 (BTD6, repacked)
                                              │
                       ┌──────────────────────▼───────────────────────┐
                       │  LemonLoader runtime (Android MelonLoader)    │
                       │  libmain injection → il2cpp bootstrap →       │
                       │  loads /BML/Mods/*.dll                        │
                       └───────────────────────────────────────────────┘
```

## Pipeline (source files)

| Step | File | Status |
|---|---|---|
| Extract installed APK | `core/ApkExtractor.kt` | ✅ implemented |
| Inject payload / strip signature | `core/ApkPatcher.kt` | ✅ zip-level injection implemented; entry-point patch TODO |
| Generate signing key | `core/KeystoreManager.kt` | ✅ implemented (BouncyCastle) |
| Sign APK (v1/v2/v3) | `core/ApkSigner.kt` | ✅ implemented (`apksig`) |
| Uninstall/install | `core/PackageInstallerHelper.kt` | ✅ implemented |
| Loader payload resolution | `core/LoaderPayload.kt` | ⚠️ TODO — binaries not bundled |
| Orchestrate the pipeline | `core/Repackager.kt` | ✅ implemented |
| Stage mods (public dir) | `core/ModInstaller.kt` | ⚠️ GitHub download TODO |
| All Files Access helper | `core/StorageAccess.kt` | ✅ implemented |
| Detect + launch game | `core/LoaderCore.kt` | ✅ implemented |

## Requirements

**Device:**

- Android 7.0+ (API 24), with Bloons TD 6 installed
- "Install unknown apps" enabled for BML
- **All Files Access** on Android 11+ (the app links you to the Settings screen)

**Developer:**

- Android Studio (latest), JDK 17, Android SDK 34

## Building

Open the repo in Android Studio and sync. If the Gradle wrapper jar/scripts are missing, Android Studio regenerates them, or run:

```bash
gradle wrapper
gradle :app:assembleDebug
```

> This is an Android (Gradle) project — not previewable or deployable through Freebuff's web hosting.

## What's next

1. **Bundle the LemonLoader payload** (`libmain.so` per ABI) as assets or a downloaded release, and fill in `LoaderPayload.load()`.
2. **Entry-point patch** — the step that makes the repacked game `dlopen` `libmain.so` at startup (LemonLoader's installer spec).
3. **Live GitHub mod catalog** (GitHub REST API over BTD6 mod topics + the BTD Mod Helper index).
4. **Mod download + verify + install** into `/BML/Mods`.
5. **Hand off the mods dir** to LemonLoader inside the repacked game.
6. Verify the most popular mods against the Android IL2CPP build.

## Porting notes

- Prefer mods that resolve game APIs through **Il2CppInterop** by name — most portable to Android.
- Mods referencing **PC-only assemblies** or hardcoded **offsets** need porting against Android's `libil2cpp.so`.
- Test only in **single-player / offline**.

## Legal / fair use

Bloons TD 6 mods are not supported by Ninja Kiwi and violate the game's Terms of Service. BML is for **offline, single-player, and private-session use only**. Modified clients can be flagged or banned. This project is an educational interoperability effort and is not affiliated with Ninja Kiwi, MelonLoader, LemonLoader, or the BTD Mod Helper authors.

## Credits

- [MelonLoader](https://github.com/LavaGang/MelonLoader) — reference loader and mod API
- [LemonLoader](https://github.com/LemonLoader) — Android MelonLoader runtime
- [BTD Mod Helper](https://github.com/gurrenm3/BTD-Mod-Helper) — BTD6 modding framework
- The BTD6 modding community
