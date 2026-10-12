package fontys.ict.teamup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- DATAKLASSEN (Verplicht om "Unresolved reference" en type-inferentie fouten op te lossen) ---

@Serializable
data class EventItem(
    val id: String,
    @SerialName("location_id") val locationId: String? = null,
    val day: String? = null,
    val time: String? = null,
    val sport: String? = null,
    @SerialName("creator_id") val creatorId: String? = null
)

@Serializable
data class LocationItem(
    val id: String,
    val name: String
)

@Serializable
data class ParticipantItem(
    @SerialName("user_id") val userId: String,
    @SerialName("event_id") val eventId: String,
    val status: String? = null
)

@Serializable
data class ProfileItem(
    val id: String,
    val username: String? = null
)

data class EventDisplayModel(
    val id: String,
    val text: String,
    val creatorName: String,
    val participantCount: Int,
    val isJoined: Boolean,
    val isCreator: Boolean
)

// --- COMPOSABLE ---

@Composable
fun EventPager(
    pagerState: PagerState,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(true) }
    var joinedEventsList by remember { mutableStateOf<List<EventDisplayModel>>(emptyList()) }
    var joinableEventsList by remember { mutableStateOf<List<EventDisplayModel>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()
    val currentUserId = supabase.auth.currentUserOrNull()?.id

    fun loadEvents() {
        coroutineScope.launch {
            try {
                isLoading = true

                val events = supabase.from("events").select().decodeList<EventItem>()
                val locations = supabase.from("locations").select().decodeList<LocationItem>()
                val locationMap = locations.associate { it.id to it.name }

                val participants = try {
                    supabase.from("event_participants").select().decodeList<ParticipantItem>()
                } catch (_: Exception) {
                    emptyList<ParticipantItem>()
                }

                val profiles = try {
                    supabase.from("profiles").select().decodeList<ProfileItem>()
                } catch (_: Exception) {
                    emptyList<ProfileItem>()
                }
                val profileMap = profiles.associate { it.id to (it.username ?: "Onbekend") }

                val joined = mutableListOf<EventDisplayModel>()
                val joinable = mutableListOf<EventDisplayModel>()

                for (event in events) {
                    val locationName = event.locationId?.let { locationMap[it] } ?: "Onbekende locatie"
                    val eventText = "${event.day ?: ""} om ${event.time ?: ""} bij $locationName (${event.sport ?: ""})"

                    val creatorName = event.creatorId?.let { profileMap[it] } ?: "Onbekende maker"

                    val eventParticipants = participants.filter { it.eventId == event.id }
                    val isUserJoined = eventParticipants.any { it.userId == currentUserId }
                    val isCreator = event.creatorId == currentUserId

                    val hasCreatorInParticipants = eventParticipants.any { it.userId == event.creatorId }
                    val participantCount = eventParticipants.size + if (!event.creatorId.isNullOrBlank() && !hasCreatorInParticipants) 1 else 0

                    val model = EventDisplayModel(
                        id = event.id,
                        text = eventText,
                        creatorName = creatorName,
                        participantCount = participantCount,
                        isJoined = isUserJoined || isCreator,
                        isCreator = isCreator
                    )

                    if (isUserJoined || isCreator) {
                        joined.add(model)
                    } else {
                        joinable.add(model)
                    }
                }

                joinedEventsList = joined
                joinableEventsList = joinable
            } catch (_: Exception) {
                // Foutafhandeling indien nodig
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadEvents()
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth()
    ) { page ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val screenHeight = maxHeight
            val dynamicSpacing = (screenHeight * 0.010f).coerceIn(6.dp, 20.dp)
            val horizontalAlignment = if (page == 0) Alignment.Start else Alignment.End

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = screenHeight)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(
                        dynamicSpacing,
                        Alignment.CenterVertically
                    ),
                    horizontalAlignment = horizontalAlignment
                ) {
                    val currentList = if (page == 0) joinedEventsList else joinableEventsList

                    if (currentList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (page == 0) "Geen ingeschreven evenementen" else "Geen beschikbare evenementen",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        currentList.forEach { eventModel ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .clickable { onEventClick(eventModel.id) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = eventModel.text,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Gemaakt door: ${eventModel.creatorName} • ${eventModel.participantCount} deelnemers",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (!eventModel.isCreator) {
                                        Spacer(modifier = Modifier.heightIn(min = 4.dp))
                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    try {
                                                        if (currentUserId != null) {
                                                            if (eventModel.isJoined) {
                                                                supabase.from("event_participants")
                                                                    .delete {
                                                                        filter {
                                                                            eq("user_id", currentUserId)
                                                                            eq("event_id", eventModel.id)
                                                                        }
                                                                    }
                                                            } else {
                                                                val payload = ParticipantItem(
                                                                    userId = currentUserId,
                                                                    eventId = eventModel.id,
                                                                    status = "joined"
                                                                )
                                                                supabase.from("event_participants")
                                                                    .insert(payload)
                                                            }
                                                            loadEvents()
                                                        }
                                                    } catch (_: Exception) {
                                                        // Foutafhandeling
                                                    }
                                                }
                                            },
                                            colors = if (eventModel.isJoined) {
                                                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                            } else {
                                                ButtonDefaults.buttonColors()
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(text = if (eventModel.isJoined) "Verlaat evenement" else "Doe mee")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}