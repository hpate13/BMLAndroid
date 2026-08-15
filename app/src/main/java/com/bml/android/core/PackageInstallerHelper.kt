package com.bml.android.core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import java.io.File
import java.io.FileInputStream

/**
 * Installs the repacked APK and uninstalls the original BTD6.
 *
 * The repacked APK is signed with a different key than the Play Store build, so
 * the original must be uninstalled first — Android will not replace an app with
 * an APK signed by a different certificate.
 */
object PackageInstallerHelper {

    const val ACTION_INSTALL_COMPLETE = "com.bml.android.action.INSTALL_COMPLETE"

    /** Asks the user to uninstall the original BTD6 (requires user confirmation). */
    fun requestUninstall(context: Context) {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:${ApkExtractor.BTD6_PACKAGE}")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /** Installs a signed APK through the [PackageInstaller] session API. */
    fun install(context: Context, apk: File) {
        val installer = context.packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        val sessionId = installer.createSession(params)
        val session = installer.openSession(sessionId)

        try {
            FileInputStream(apk).use { input ->
                session.openWrite("repacked.apk", 0, apk.length()).use { output ->
                    input.copyTo(output)
                    session.fsync(output)
                }
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                sessionId,
                Intent(ACTION_INSTALL_COMPLETE).setPackage(context.packageName),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            session.commit(pendingIntent.intentSender)
        } finally {
            session.close()
        }
    }
}
