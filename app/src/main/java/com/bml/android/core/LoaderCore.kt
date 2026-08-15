package com.bml.android.core

import android.content.Context
import android.content.Intent

/**
 * Thin wrapper around LemonLoader — the Android runtime for the MelonLoader
 * mod API (https://github.com/LemonLoader).
 *
 * BML does not reimplement IL2CPP hooking; it delegates to LemonLoader for
 * that, exactly like a PC mod manager delegates to MelonLoader. This object is
 * responsible for detecting the game, detecting the loader bootstrap, and
 * launching BTD6 with the loader active.
 */
object LoaderCore {

    fun isGameInstalled(context: Context): Boolean =
        runCatching {
            context.packageManager.getPackageInfo(ModInstaller.BTD6_PACKAGE, 0)
        }.isSuccess

    fun launchGame(context: Context): Boolean {
        val intent: Intent = context.packageManager
            .getLaunchIntentForPackage(ModInstaller.BTD6_PACKAGE)
            ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return true
    }

    fun isLoaderBootstrapped(context: Context): Boolean {
        // TODO: detect LemonLoader's bootstrap (injected libmain / MelonLoader
        // dir) for the installed BTD6 copy and report whether mods will load.
        return false
    }
}
