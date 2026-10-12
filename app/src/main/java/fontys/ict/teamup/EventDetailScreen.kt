package fontys.ict.teamup

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventDetailModel(
    val id: String,
    val sport: String,
    val day: String,
    val time: String,
    @SerialName("captain_id")
    val captainId: String?,
    @SerialName("creator_id")
    val creatorId: String?
)

@Composable
fun EventDetailView(
    eventId: String,
    onBackClick: () -> Unit
) {
    var event by remember { mutableStateOf<EventDetailModel?>(null) }
    var participants by remember { mutableStateOf<List<ProfileItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val currentUserId = supabase.auth.currentUserOrNull()?.id
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(eventId) {
        coroutineScope.launch {
            try {
                val fetchedEvent = supabase.from("events")
                    .select {
                        filter { eq("id", eventId) }
                    }
                    .decodeSingle<EventDetailModel>()
                event = fetchedEvent

                val eventParts = supabase.from("event_participants")
                    .select {
                        filter { eq("event_id", eventId) }
                    }
                    .decodeList<ParticipantItem>()

                val userIds = eventParts.map { it.userId }.toMutableList()
                if (fetchedEvent.creatorId != null && !userIds.contains(fetchedEvent.creatorId)) {
                    userIds.add(fetchedEvent.creatorId)
                }
                if (fetchedEvent.captainId != null && !userIds.contains(fetchedEvent.captainId)) {
                    userIds.add(fetchedEvent.captainId)
                }

                if (userIds.isNotEmpty()) {
                    // OPGELOST: Haal alleen de profielen op die bij deze specifieke deelnemers horen
                    participants = supabase.from("profiles")
                        .select {
                            filter {
                                isIn("id", userIds)
                            }
                        }
                        .decodeList<ProfileItem>()
                }
            } catch (e: Exception) {
                android.util.Log.e("EventDetailView", "Fout bij laden evenement", e)
                Toast.makeText(context, "Fout bij laden evenement", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Evenement Details", style = MaterialTheme.typography.titleLarge)
                Button(onClick = onBackClick) {
                    Text("Terug")
                }
            }

            val currentEvent = event
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (currentEvent != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Sport: ${currentEvent.sport}", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Wanneer: ${currentEvent.day} om ${currentEvent.time}", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Deelnemers:", style = MaterialTheme.typography.bodyLarge)

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(participants) { participant ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = participant.username ?: "Onbekend")

                                val isUserCaptain = currentEvent.captainId == currentUserId
                                val isSelf = participant.id == currentUserId

                                if (isUserCaptain && !isSelf) {
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                try {
                                                    supabase.postgrest.rpc(
                                                        "process_endorsement_and_close_event",
                                                        mapOf(
                                                            "p_event_id" to eventId,
                                                            "p_receiver_id" to participant.id
                                                        )
                                                    )

                                                    Toast.makeText(context, "Endorsement gegeven! Elo toegekend.", Toast.LENGTH_SHORT).show()
                                                    onBackClick()
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Fout: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }
                                    ) {
                                        Text("Geef Endorsement 🌟")
                                    }
                                }
                            }
                        }
                    }
                }

                if (currentEvent.captainId == currentUserId) {
                    Text(
                        text = "Jij bent de kapitein van dit evenement! Kies een medespeler om een endorsement te geven.",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}