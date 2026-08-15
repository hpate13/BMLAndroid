package com.bml.android.core

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Repacks the APK, injecting the LemonLoader payload (`libmain.so` per ABI) and
 * stripping the original signature so the result can be re-signed.
 *
 * The rewrite through [ZipOutputStream] also drops the old APK Signing Block,
 * leaving a clean, unsigned APK for [ApkSigner].
 *
 * TODO: the *entry-point* patch that makes the game dlopen `libmain.so` at
 * startup is applied by the LemonLoader payload spec (manifest/smali/`libil2cpp`
 * hook). That step happens here once the payload artifacts are bundled.
 */
object ApkPatcher {

    fun inject(input: File, output: File, payloads: Map<String, ByteArray>) {
        val outStream = FileOutputStream(output)
        val zipOut = ZipOutputStream(outStream)
        val seen = mutableSetOf<String>()

        ZipFile(input).use { zip ->
            val entries = zip.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (isSignatureEntry(entry.name)) continue
                copyEntry(zip, zipOut, entry, entry.name, seen)
            }
        }

        for ((abi, bytes) in payloads) {
            val name = "lib/$abi/libmain.so"
            if (seen.add(name)) {
                zipOut.putNextEntry(ZipEntry(name)) // DEFLATED: safe for install
                zipOut.write(bytes)
                zipOut.closeEntry()
            }
        }

        zipOut.finish()
        zipOut.close()
        outStream.close()
    }

    private fun copyEntry(
        zip: ZipFile,
        out: ZipOutputStream,
        entry: ZipEntry,
        name: String,
        seen: MutableSet<String>,
    ) {
        if (!seen.add(name)) return
        val newEntry = ZipEntry(name)
        if (entry.method == ZipEntry.STORED) {
            newEntry.method = ZipEntry.STORED
            newEntry.size = entry.size
            newEntry.compressedSize = entry.compressedSize
            newEntry.crc = entry.crc
        }
        out.putNextEntry(newEntry)
        zip.getInputStream(entry).use { it.copyTo(out) }
        out.closeEntry()
    }

    private fun isSignatureEntry(name: String): Boolean {
        if (!name.startsWith("META-INF/")) return false
        if (name == "META-INF/MANIFEST.MF") return true
        return name.endsWith(".SF") || name.endsWith(".RSA") ||
            name.endsWith(".DSA") || name.endsWith(".EC")
    }
}
