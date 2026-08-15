package com.bml.android.core

import com.android.apksig.ApkSigner
import java.io.File

/**
 * Signs the repacked APK with v1 (JAR) + v2 + v3 signatures using the locally
 * generated keystore, via Google's `apksig` library.
 */
object ApkSigner {

    fun sign(
        input: File,
        output: File,
        identity: KeystoreManager.SigningIdentity,
        minSdkVersion: Int = 24,
    ) {
        val signerConfig = ApkSigner.SignerConfig.Builder("BML")
            .setPrivateKey(identity.privateKey)
            .setCertificates(listOf(identity.certificate))
            .build()

        ApkSigner.Builder(listOf(signerConfig))
            .setInputApk(input)
            .setOutputApk(output)
            .setMinSdkVersion(minSdkVersion)
            .setV1SigningEnabled(true)
            .setV2SigningEnabled(true)
            .setV3SigningEnabled(true)
            .build()
            .sign()
    }
}
