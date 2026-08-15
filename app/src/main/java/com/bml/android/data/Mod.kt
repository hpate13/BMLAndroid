package com.bml.android.data

/**
 * A BTD6 mod distributed as a MelonLoader assembly on GitHub.
 *
 * @param repo GitHub repository in "owner/name" form.
 * @param releaseAssetPattern Glob used to pick the mod assembly out of the
 *   repository's latest release assets.
 */
data class Mod(
    val id: String,
    val name: String,
    val author: String,
    val description: String,
    val repo: String,
    val releaseAssetPattern: String = "*.dll",
)
