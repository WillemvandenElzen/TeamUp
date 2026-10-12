package fontys.ict.teamup

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    currentThemeMode: String = "System",
    onThemeModeChanged: (String) -> Unit = {},
    onLogout: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showUsernameDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var newUsernameInput by remember { mutableStateOf("") }

    if (showUsernameDialog) {
        AlertDialog(
            onDismissRequest = { showUsernameDialog = false },
            title = { Text("Wijzig gebruikersnaam") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Kies een nieuwe weergavenaam voor je profiel.")
                    OutlinedTextField(
                        value = newUsernameInput,
                        onValueChange = { newUsernameInput = it },
                        label = { Text("Nieuwe gebruikersnaam") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newUsernameInput.isBlank()) {
                            Toast.makeText(context, "Vul een geldige naam in", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        coroutineScope.launch {
                            try {
                                val currentUser = supabase.auth.currentUserOrNull()
                                if (currentUser != null) {
                                    val payload = UserProfile(
                                        id = currentUser.id,
                                        username = newUsernameInput.trim()
                                    )
                                    supabase.from("profiles").upsert(payload)
                                    Toast.makeText(context, "Gebruikersnaam bijgewerkt!", Toast.LENGTH_SHORT).show()
                                    showUsernameDialog = false
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Fout bij opslaan: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                ) {
                    Text("Opslaan")
                }
            },
            dismissButton = {
                Button(onClick = { showUsernameDialog = false }) {
                    Text("Annuleren")
                }
            }
        )
    }

    if (showThemeDialog) {
        val options = listOf("Automatisch (Systeem)", "Licht", "Donker")
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Thema selecteren") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    options.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val mapped = when (option) {
                                        "Licht" -> "Light"
                                        "Donker" -> "Dark"
                                        else -> "System"
                                    }
                                    onThemeModeChanged(mapped)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val isSelected = when (currentThemeMode) {
                                "Light" -> option == "Licht"
                                "Dark" -> option == "Donker"
                                else -> option == "Automatisch (Systeem)"
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    val mapped = when (option) {
                                        "Licht" -> "Light"
                                        "Donker" -> "Dark"
                                        else -> "System"
                                    }
                                    onThemeModeChanged(mapped)
                                    showThemeDialog = false
                                }
                            )
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Instellingen") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Terug"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SettingItemRow(
                    title = "Gebruikersnaam wijzigen",
                    subtitle = "Pas je zichtbare naam aan in de app",
                    onClick = {
                        newUsernameInput = ""
                        showUsernameDialog = true
                    }
                )

                SettingItemRow(
                    title = "Thema",
                    subtitle = when (currentThemeMode) {
                        "Light" -> "Licht"
                        "Dark" -> "Donker"
                        else -> "Automatisch (Systeem)"
                    },
                    onClick = { showThemeDialog = true }
                )

                SettingItemRow(
                    title = "Uitloggen",
                    subtitle = "Beëindig je huidige sessie",
                    onClick = {
                        coroutineScope.launch {
                            try {
                                supabase.auth.signOut()
                                onLogout()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Uitloggen mislukt: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SettingItemRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}