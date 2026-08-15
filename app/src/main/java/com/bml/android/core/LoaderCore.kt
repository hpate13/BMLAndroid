package com.bml.android.core

import android.content.Context
import android.content.Intent

/**
 * Game detection and launching for the no-root repackaging flow. The runtime
 * hooking itself is handled by LemonLoader, injected into the repacked game by
 * [Repackager].
 */
object LoaderCore {

    fun isGameInstalled(context: Context): Boolean =
        runCatching {
            context.packageManager.getPackageInfo(ApkExtractor.BTD6_PACKAGE, 0)
        }.isSuccess

    fun isRepacked(context: Context): Boolean = try {
        // TODO: detect whether the installed BTD6 copy is BML's repacked build
        // (e.g. compare its signing certificate against our keystore).
        false
    } catch (t: Throwable) {
        false
    }

    fun launchGame(context: Context): Boolean {
        val intent: Intent = context.packageManager
            .getLaunchIntentForPackage(ApkExtractor.BTD6_PACKAGE)
            ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return true
    }
}
