package com.bml.android.core

import android.content.Context
import android.os.Environment
import com.bml.android.data.Mod
import java.io.File

/**
 * Stages mod assemblies in a public directory (`/storage/emulated/0/BML/Mods`)
 * that both BML and the repacked BTD6 (running LemonLoader) can read, so no
 * root is required.
 *
 * Requires "All files access" on Android 11+ (see [StorageAccess]). LemonLoader
 * inside the repacked game must be configured to scan this directory.
 */
object ModInstaller {

    fun modsDir(context: Context): File =
        File(Environment.getExternalStorageDirectory(), "BML/Mods").apply { mkdirs() }

    fun install(mod: Mod, context: Context): Boolean = try {
        // TODO: download the mod's latest release asset from GitHub (matching
        // `mod.releaseAssetPattern`), verify it, then write it into modsDir().
        false
    } catch (t: Throwable) {
        false
    }

    fun uninstall(mod: Mod, context: Context): Boolean = try {
        // TODO: remove the staged assembly for `mod` from modsDir().
        false
    } catch (t: Throwable) {
        false
    }
}
