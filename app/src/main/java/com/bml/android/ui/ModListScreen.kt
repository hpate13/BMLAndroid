package com.bml.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bml.android.core.LoaderCore
import com.bml.android.core.ModInstaller
import com.bml.android.data.Mod
import com.bml.android.data.ModCatalog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModListScreen() {
    val context = LocalContext.current
    var mods by remember { mutableStateOf(ModCatalog.seed) }
    var installed by remember { mutableStateOf(emptySet<String>()) }
    var status by remember { mutableStateOf("") }
    val gameInstalled = remember { LoaderCore.isGameInstalled(context) }
    val loaderReady = remember { LoaderCore.isLoaderBootstrapped(context) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("BML — Bloons Mod Loader") })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatusBanner(
                gameInstalled = gameInstalled,
                loaderReady = loaderReady,
                status = status,
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(mods, key = { it.id }) { mod ->
                    val isInstalled = mod.id in installed
                    ModRow(
                        mod = mod,
                        isInstalled = isInstalled,
                        onToggle = {
                            val ok = if (isInstalled) {
                                ModInstaller.uninstall(mod, context)
                            } else {
                                ModInstaller.install(mod, context)
                            }
                            installed = if (ok) {
                                if (isInstalled) installed - mod.id else installed + mod.id
                            } else {
                                installed
                            }
                            status = if (ok) {
                                "${if (isInstalled) "Removed" else "Installed"} ${mod.name}"
                            } else {
                                "Could not ${if (isInstalled) "remove" else "install"} ${mod.name} (see docs)"
                            }
                        },
                    )
                }
            }

            Button(
                onClick = {
                    status = if (LoaderCore.launchGame(context)) {
                        "Launching BTD6…"
                    } else {
                        "BTD6 is not installed on this device."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Launch BTD6 with mods")
            }
        }
    }
}

@Composable
private fun StatusBanner(
    gameInstalled: Boolean,
    loaderReady: Boolean,
    status: String,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = when {
                    !gameInstalled -> "BTD6 not detected"
                    !loaderReady -> "BTD6 found — LemonLoader bootstrap not wired yet"
                    else -> "BTD6 + loader ready"
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            if (status.isNotEmpty()) {
                Text(status, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ModRow(
    mod: Mod,
    isInstalled: Boolean,
    onToggle: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(mod.name, style = MaterialTheme.typography.titleMedium)
                Text("by ${mod.author}", style = MaterialTheme.typography.bodySmall)
                Text(mod.description, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.width(12.dp))
            if (isInstalled) {
                OutlinedButton(onClick = onToggle) { Text("Remove") }
            } else {
                Button(onClick = onToggle) { Text("Install") }
            }
        }
    }
}
