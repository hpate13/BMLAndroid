package com.bml.android.core

import android.content.Context
import com.bml.android.data.Mod
import java.io.File

/**
 * Handles placing mod assemblies where the BTD6 game process looks for them.
 *
 * BTD6's package id is `com.ninjakiwi.bloonstd6`. Under LemonLoader (the Android
 * MelonLoader runtime) mods are discovered in the game's `MelonLoader/Mods`
 * folder, mirroring MelonLoader's `Mods/` convention on PC.
 *
 * Access to that folder requires one of:
 *  - a rooted device (read/write `/data/data/com.ninjakiwi.bloonstd6`), or
 *  - a repackaged/patchable copy of the game installed through LemonLoader's
 *    installer workflow, with mods staged through shared storage
 *    (`/storage/emulated/0/Android/data/com.ninjakiwi.bloonstd6/files`).
 *
 * The functions below model that contract; the device-specific I/O hooks are
 * marked TODO until the LemonLoader install workflow is wired in.
 */
object ModInstaller {

    const val BTD6_PACKAGE = "com.ninjakiwi.bloonstd6"

    /** Direct data-dir path used on rooted devices. */
    fun modsDir(context: Context): File =
        File("/data/data/$BTD6_PACKAGE/files/MelonLoader/Mods")

    fun install(mod: Mod, context: Context): Boolean = try {
        // TODO: download the mod's latest release asset from GitHub (matching
        // `mod.releaseAssetPattern`), verify it, then copy it into modsDir().
        false
    } catch (t: Throwable) {
        false
    }

    fun uninstall(mod: Mod, context: Context): Boolean = try {
        // TODO: remove the installed assembly for `mod` from modsDir().
        false
    } catch (t: Throwable) {
        false
    }
}
