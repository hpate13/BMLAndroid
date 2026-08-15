# BML — Bloons TD 6 Mod Loader for Android

A MelonLoader-style mod loader for **Bloons TD 6**, purpose-built for **Android**, that runs the BTD6 mods the community already distributes on GitHub.

> **Status: early scaffold.** This repo currently contains the project structure, an Android app shell, and the architecture for the loader. The deep runtime work is tracked in [Roadmap](#roadmap).

---

## What this is

BML is an Android app that installs and manages mods for Bloons TD 6 and launches the game with them enabled — the Android equivalent of the MelonLoader workflow PC players use for BTD6.

It is deliberately scoped to Bloons TD 6:

- A curated **GitHub catalog** of BTD6 mods
- One-tap **install / remove** of mod assemblies
- **Game detection** and a **launcher** that boots BTD6 with the loader active

## Relationship to MelonLoader and LemonLoader

- **[MelonLoader](https://github.com/LavaGang/MelonLoader)** (LavaGang) is the universal Unity mod loader that the BTD6 PC community standardizes on. It supports both Mono and IL2CPP games; its Android/Oculus support is still marked *work-in-progress*.
- **[LemonLoader](https://github.com/LemonLoader)** is the Android port of MelonLoader. It exposes the **same MelonLoader mod API** — `MelonMod` base class, attribute registration, Harmony patching, and Il2CppInterop — inside Unity games running on Android, for both Mono and IL2CPP.
- **Bloons TD 6 is an IL2CPP Unity game** on both PC and Android. On PC you install MelonLoader into the game folder and drop mods into `Mods/`. BML brings that same experience to Android on top of LemonLoader.

**BML does not reimplement IL2CPP hooking from scratch.** It reuses LemonLoader as the runtime engine — the same way a PC mod manager reuses MelonLoader — and adds what is specific to BTD6: the mod catalog, install/removal, game detection, and launching.

## The mod ecosystem

Most BTD6 mods are C# assemblies built against:

- **MelonLoader's API** (`MelonMod`, mod attributes, `Harmony`)
- **[BTD Mod Helper](https://github.com/gurrenm3/BTD-Mod-Helper)** (gurrenm3) — the standard modding framework
- **Il2CppInterop** — for calling into the game's IL2CPP code

They are distributed as release DLLs on GitHub. The "same mods" the PC community uses can run on Android because LemonLoader provides a compatible MelonLoader API — with one important caveat:

> The Android build of BTD6 is a **different IL2CPP binary** than the PC build. Mods that resolve game methods **by name** (via Il2CppInterop) generally work across platforms, while mods that hardcode PC memory offsets, PC-only assemblies, or PC-specific UI need porting. See [Porting notes](#porting-notes).

## Architecture

```
┌──────────────────────────── BML app (Kotlin / Jetpack Compose) ────────────────────────────┐
│  ModCatalog (GitHub index)  →  ModInstaller (stage DLLs)  →  LoaderCore (detect + launch)  │
└─────────────────────────────────────────────┬───────────────────────────────────────────────┘
                                              │ launches
                                com.ninjakiwi.bloonstd6 (BTD6)
                                              │
                       ┌──────────────────────▼───────────────────────┐
                       │  LemonLoader runtime (Android MelonLoader)    │
                       │  libmain injection → il2cpp bootstrap →       │
                       │  loads MelonLoader/Mods/*.dll                 │
                       └───────────────────────────────────────────────┘
```

## Requirements

**End user (device):**

- An Android device with Bloons TD 6 installed (`com.ninjakiwi.bloonstd6`)
- A **rooted device**, or the game installed through LemonLoader's **repackaging** workflow
- "Install unknown apps" enabled, plus storage access for staging mods

**Developer:**

- Android Studio (latest), JDK 17, Android SDK (compile/target SDK 34)
- The Android NDK is only required once the native module is wired in

## Building

Open the repo in Android Studio and sync. If the Gradle wrapper jar/scripts are missing, Android Studio regenerates them automatically, or run:

```bash
gradle wrapper
gradle :app:assembleDebug
```

> Note: this is an **Android (Gradle) project**, not a web app, so it is not previewable or deployable through Freebuff's web preview/hosting.

## Project layout

```
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── cpp/                    # native loader hook (placeholder)
│       │   ├── CMakeLists.txt
│       │   └── loader.cpp
│       ├── java/com/bml/android/
│       │   ├── MainActivity.kt
│       │   ├── ui/                 # Compose UI (mod list)
│       │   ├── data/               # Mod model + GitHub catalog
│       │   └── core/               # ModInstaller + LoaderCore
│       └── res/
├── gradle/                         # version catalog + wrapper properties
└── settings.gradle.kts
```

## What's implemented vs. what's next

**Implemented (scaffold):**

- README and project structure
- Gradle + Jetpack Compose app shell (mod list UI, catalog seed, install/remove/launch plumbing)
- `LoaderCore` wrappers around LemonLoader + BTD6 game detection/launch
- `ModInstaller` with correct BTD6 paths and the rooted/repackaged workflow contract
- Native placeholder documenting the in-process injection handoff

**Roadmap (the real work):**

1. Wire LemonLoader's install/bootstrap into `ModInstaller` / `LoaderCore`
2. Live GitHub mod catalog (GitHub REST API over the BTD6 mod topics + the BTD Mod Helper community index)
3. Mod asset download, checksumming, and version tracking
4. Native module: BTD6-specific patches for mods that need them (requires NDK)
5. Port/verify the most popular mods against the Android IL2CPP build

## Porting notes

- Prefer mods that resolve game APIs through **Il2CppInterop** by name — these are the most portable.
- Mods referencing **PC-only assemblies** (e.g. desktop-only UI, Steam integration) will not load on Android.
- Mods that patch methods found by **offset/address** must be updated against the Android `libil2cpp.so`.
- Test only in **single-player / offline** — never in ranked or public online play.

## Legal / fair use

Bloons TD 6 mods are not supported by Ninja Kiwi and violate the game's Terms of Service. BML is for **offline, single-player, and private-session use only**. Using mods in ranked or public online play can flag or ban your account.

This project is an educational interoperability effort and is not affiliated with or endorsed by Ninja Kiwi, MelonLoader, LemonLoader, or the BTD Mod Helper authors.

## Credits

- [MelonLoader](https://github.com/LavaGang/MelonLoader) — reference loader and mod API
- [LemonLoader](https://github.com/LemonLoader) — Android MelonLoader runtime
- [BTD Mod Helper](https://github.com/gurrenm3/BTD-Mod-Helper) — BTD6 modding framework
- The BTD6 modding community
