package fontys.ict.teamup

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Serializable
data class LocationInsert(
    val name: String,
    val location: String
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
    @SerialName("end_time")
    val endTime: String,
    @SerialName("location_id")
    val locationId: String,
    @SerialName("creator_id")
    val creatorId: String?
)

@Composable
fun EventCreateView(
    onBackClick: () -> Unit
) {
    var placeName by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var sport by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        day = formatter.format(Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Event Create Screen",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Normaal invoerveld voor locatie/plaatsnaam
            OutlinedTextField(
                value = placeName,
                onValueChange = { placeName = it },
                label = { Text("Place / Location") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = day,
                onValueChange = {},
                readOnly = true,
                label = { Text("Day (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = time,
                onValueChange = {},
                readOnly = true,
                label = { Text("Time (Select start time)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val calendar = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                                val endCalendar = Calendar.getInstance().apply {
                                    set(Calendar.HOUR_OF_DAY, hour)
                                    set(Calendar.MINUTE, minute)
                                    add(Calendar.MINUTE, 5)
                                }
                                endTime = String.format(
                                    Locale.getDefault(),
                                    "%02d:%02d",
                                    endCalendar.get(Calendar.HOUR_OF_DAY),
                                    endCalendar.get(Calendar.MINUTE)
                                )
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                enabled = false
            )
            Spacer(modifier = Modifier.height(8.dp))

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
                        if (placeName.isBlank() || time.isBlank() || endTime.isBlank() || day.isBlank() || sport.isBlank()) {
                            Toast.makeText(
                                context,
                                "Please fill out all fields",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        isLoading = true
                        coroutineScope.launch {
                            try {
                                val currentUserId = supabase.auth.currentUserOrNull()?.id

                                // 1. Insert the typed place name into the 'locations' table first
                                // to get a valid UUID back from the database
                                val locationPayload = LocationInsert(
                                    name = placeName,
                                    location = "SRID=4326;POINT(5.6728 51.6053)" // Default fallback coordinates
                                )

                                val insertedLocation = supabase.from("locations")
                                    .insert(locationPayload) { select() }
                                    .decodeSingle<LocationResponse>()

                                // 2. Use the generated UUID for the event
                                val eventPayload = EventInsert(
                                    sport = sport,
                                    day = day,
                                    time = time,
                                    endTime = endTime,
                                    locationId = insertedLocation.id,
                                    creatorId = currentUserId
                                )

                                supabase.from("events").insert(eventPayload)

                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Event created successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onBackClick()
                            } catch (e: Exception) {
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Error: ${e.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
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
