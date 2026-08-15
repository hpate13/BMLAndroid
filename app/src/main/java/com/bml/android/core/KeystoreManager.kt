package com.bml.android.core

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.util.Date

/**
 * Generates (or reuses) a local signing key for the repacked game. The key is
 * not secret in any security sense — it only needs to exist so the repacked APK
 * has a valid signature and installs as a normal, updated-in-place package.
 */
object KeystoreManager {

    private const val ALIAS = "bml"
    private const val PASSWORD = "bml-btd6-repack"

    data class SigningIdentity(
        val privateKey: PrivateKey,
        val certificate: X509Certificate,
    )

    fun getOrCreate(keystoreFile: File): SigningIdentity {
        val password = PASSWORD.toCharArray()
        val keyStore = if (keystoreFile.exists()) {
            KeyStore.getInstance("PKCS12").apply {
                load(keystoreFile.inputStream(), password)
            }
        } else {
            generate(keystoreFile, password)
        }

        val privateKey = keyStore.getKey(ALIAS, password) as PrivateKey
        val certificate = keyStore.getCertificate(ALIAS) as X509Certificate
        return SigningIdentity(privateKey, certificate)
    }

    private fun generate(file: File, password: CharArray): KeyStore {
        val keyPair = KeyPairGenerator.getInstance("RSA").apply {
            initialize(2048)
        }.generateKeyPair()

        val subject = X500Name("CN=BML Android, OU=BML, O=BML, C=US")
        val now = Date()
        val builder = JcaX509v3CertificateBuilder(
            subject,
            BigInteger.valueOf(now.time),
            Date(now.time - 86_400_000L), // valid since yesterday
            Date(now.time + 30L * 365 * 86_400_000L), // ~30 years
            subject,
            keyPair.public,
        )
        val signer = JcaContentSignerBuilder("SHA256withRSA").build(keyPair.private)
        val certificate = JcaX509CertificateConverter().getCertificate(builder.build(signer))

        val keyStore = KeyStore.getInstance("PKCS12")
        keyStore.load(null, null)
        keyStore.setKeyEntry(ALIAS, keyPair.private, password, arrayOf(certificate))

        file.parentFile?.mkdirs()
        FileOutputStream(file).use { keyStore.store(it, password) }
        return keyStore
    }
}
