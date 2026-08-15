package com.bml.android.core

import android.content.Context
import java.io.File

/**
 * Copies the installed BTD6 base APK into app-private storage so it can be
 * patched and re-signed.
 *
 * This works without root on most devices because the installed APK file is
 * world-readable through [android.content.pm.ApplicationInfo.publicSourceDir].
 */
object ApkExtractor {

    const val BTD6_PACKAGE = "com.ninjakiwi.bloonstd6"

    fun extract(context: Context, destination: File): File {
        val appInfo = runCatching {
            context.packageManager.getApplicationInfo(BTD6_PACKAGE, 0)
        }.getOrElse {
            throw IllegalStateException("BTD6 is not installed on this device.", it)
        }

        val source = File(appInfo.publicSourceDir ?: appInfo.sourceDir)
        check(source.exists() && source.canRead()) {
            "Cannot read the BTD6 APK at $source. This device blocks APK extraction."
        }

        destination.parentFile?.mkdirs()
        source.copyTo(destination, overwrite = true)
        return destination
    }
}
