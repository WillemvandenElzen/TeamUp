package fontys.ict.teamup

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val username: String
)

@Composable
fun `Event-view`(
    onSettingsClick: () -> Unit,
    onEventCreateClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onEventClick: (String) -> Unit // <-- TOEGEVOEGD HIER
) {
    val topInsetPadding = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()
    val pagerState = rememberPagerState(pageCount = { 2 })

    var username by remember { mutableStateOf("Gebruiker") }
    var showUsernameDialog by remember { mutableStateOf(false) }
    var newUsernameInput by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            val currentUser = supabase.auth.currentUserOrNull()
            if (currentUser != null) {
                val profile = supabase.from("profiles")
                    .select(Columns.list("id, username")) {
                        filter { eq("id", currentUser.id) }
                    }
                    .decodeSingleOrNull<UserProfile>()

                if (profile?.username.isNullOrBlank()) {
                    showUsernameDialog = true
                } else {
                    username = profile!!.username
                }
            }
        } catch (_: Exception) {
            val email = supabase.auth.currentUserOrNull()?.email
            if (!email.isNullOrBlank()) {
                username = email.substringBefore("@")
            }
        }
    }

    if (showUsernameDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Kies een gebruikersnaam") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Voer een gebruikersnaam in zodat anderen je kunnen herkennen.")
                    OutlinedTextField(
                        value = newUsernameInput,
                        onValueChange = { newUsernameInput = it },
                        label = { Text("Gebruikersnaam") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newUsernameInput.isBlank()) {
                            Toast.makeText(context, "Vul een gebruikersnaam in", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        coroutineScope.launch {
                            try {
                                val currentUser = supabase.auth.currentUserOrNull()
                                if (currentUser != null) {
                                    val profilePayload = UserProfile(
                                        id = currentUser.id,
                                        username = newUsernameInput.trim()
                                    )

                                    supabase.from("profiles").upsert(profilePayload)

                                    username = newUsernameInput.trim()
                                    showUsernameDialog = false
                                    Toast.makeText(context, "Gebruikersnaam opgeslagen!", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Fout: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                ) {
                    Text("Opslaan")
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topInsetPadding + 8.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Hey, $username",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "Filters: [50km] [Basketbal]",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val sectionTitle = if (pagerState.currentPage == 0) {
                        "Ingeschreven evenementen:"
                    } else {
                        "Beschikbare evenementen:"
                    }

                    Text(
                        text = sectionTitle,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Leaderboard",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onLeaderboardClick() }
                            .size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onSettingsClick() }
                            .size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            EventPager(
                pagerState = pagerState,
                onEventClick = onEventClick, // <-- DOORGEGEVEN AAN EVENTPAGER HIER[cite: 6]
                modifier = Modifier.weight(1f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Maak een evenement aan!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onEventCreateClick() }
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(2) { index ->
                        val isSelected = pagerState.currentPage == index
                        val dotWidth = if (isSelected) 24.dp else 8.dp
                        val dotColor =
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray

                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(dotWidth)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                    }
                }
            }
        }
    }
}