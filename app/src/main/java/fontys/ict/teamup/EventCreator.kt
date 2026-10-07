package fontys.ict.teamup

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationInsert(
    val name: String,
    val location: String // PostGIS WKT format e.g., 'SRID=4326;POINT(longitude latitude)'
)

@Serializable
data class LocationResponse(
    val id: String
)

@Serializable
data class EventInsert(
    val sport: String,
    val day: String,
    val time: String,
    @SerialName("location_id")
    val locationId: String,
    @SerialName("creator_id")
    val creatorId: String?
)

@Composable
fun EventCreateView(onBackClick: () -> Unit) {
    var place by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var sport by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Event Create Screen",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = place,
                onValueChange = { place = it },
                label = { Text("Place (e.g. 'T Slotje)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Time (e.g. 13:00)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = day,
                onValueChange = { day = it },
                label = { Text("Day (e.g. Maandag)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = sport,
                onValueChange = { sport = it },
                label = { Text("Sport (e.g. Basketbal)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onBackClick() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(text = "Cancel")
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (place.isBlank() || time.isBlank() || day.isBlank() || sport.isBlank()) {
                            Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        coroutineScope.launch {
                            try {
                                val currentUserId = supabase.auth.currentUserOrNull()?.id

                                // 1. Insert location first
                                val locationPayload = LocationInsert(
                                    name = place,
                                    location = "SRID=4326;POINT(5.6728 51.6053)"
                                )

                                val insertedLocation = supabase.from("locations")
                                    .insert(locationPayload) {
                                        select()
                                    }
                                    .decodeSingle<LocationResponse>()

                                // 2. Insert the event linked to the location and creator using camelCase parameters
                                val eventPayload = EventInsert(
                                    sport = sport,
                                    day = day,
                                    time = time,
                                    locationId = insertedLocation.id,
                                    creatorId = currentUserId
                                )

                                supabase.from("events").insert(eventPayload)

                                isLoading = false
                                Toast.makeText(context, "Event created successfully!", Toast.LENGTH_SHORT).show()
                                onBackClick()
                            } catch (e: Exception) {
                                isLoading = false
                                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(text = if (isLoading) "Bezig..." else "Opslaan")
                }
            }
        }
    }
}