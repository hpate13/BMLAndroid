package com.bml.android.data

/**
 * Seed catalog of BTD6 mods distributed on GitHub.
 *
 * TODO: Replace this hardcoded seed with a live index. The plan is to query the
 * GitHub REST API for repositories under the BTD6 mod topics and/or pull the
 * community list used by the BTD Mod Helper ecosystem, then cache results
 * locally. Verified mod entries should be curated by hand to avoid loading
 * untrusted assemblies.
 */
object ModCatalog {

    val seed: List<Mod> = listOf(
        Mod(
            id = "btd-mod-helper",
            name = "BTD Mod Helper",
            author = "gurrenm3",
            description = "The core framework most BTD6 mods depend on. Install this first.",
            repo = "gurrenm3/BTD-Mod-Helper",
        ),
        // TODO: Add curated, verified mod entries here.
    )
}
