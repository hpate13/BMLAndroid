package com.bml.android.core

import android.content.Context
import java.io.File

/**
 * The no-root modding pipeline:
 *
 *   1. extract the installed BTD6 APK
 *   2. inject the LemonLoader payload
 *   3. sign with a locally generated keystore
 *   4. (caller) uninstall the original + install the repacked copy
 *
 * Baking the loader into the game itself is what removes the need for root.
 */
object Repackager {

    fun build(context: Context): Result<File> = runCatching {
        val workDir = File(context.filesDir, "repack").apply { mkdirs() }

        val originalApk = File(workDir, "btd6-original.apk")
        ApkExtractor.extract(context, originalApk)

        val payloads = LoaderPayload.load(context)
        check(payloads.isNotEmpty()) {
            "LemonLoader payload not available. Bundle libmain.so for " +
                (LoaderPayload.abi ?: "this device") + " to continue."
        }

        val patchedApk = File(workDir, "btd6-patched.apk")
        ApkPatcher.inject(originalApk, patchedApk, payloads)

        val signedApk = File(workDir, "btd6-repacked.apk")
        val identity = KeystoreManager.getOrCreate(File(workDir, "bml.keystore"))
        ApkSigner.sign(patchedApk, signedApk, identity)

        signedApk
    }
}
