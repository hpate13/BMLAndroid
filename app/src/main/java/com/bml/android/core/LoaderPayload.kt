package com.bml.android.core

import android.content.Context
import android.os.Build

/**
 * Resolves the LemonLoader payload that gets injected into the repacked APK.
 *
 * LemonLoader ships a prebuilt `libmain.so` (plus an entry-point patch) per ABI.
 * BML does not build the loader itself — it bundles or downloads those release
 * artifacts. The binaries are intentionally not committed to this repo.
 *
 * TODO: load `libmain.so` for the active ABI from assets (assets/loader/<abi>/libmain.so)
 * or from a downloaded LemonLoader release, and return mapOf(abi to bytes).
 */
object LoaderPayload {

    val abi: String? = Build.SUPPORTED_ABIS.firstOrNull()

    fun load(context: Context): Map<String, ByteArray> {
        val activeAbi = abi ?: return emptyMap()
        // TODO: return mapOf(activeAbi to payloadBytes)
        return emptyMap()
    }
}
