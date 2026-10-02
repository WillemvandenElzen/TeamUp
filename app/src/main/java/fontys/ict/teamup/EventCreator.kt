package fontys.ict.teamup

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EventCreateView(onBackClick: () -> Unit) {
    var place by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var sport by remember { mutableStateOf("") }

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
                label = { Text("Place") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Time") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = day,
            onValueChange = { day = it },
            label = { Text("day") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = sport,
            onValueChange = { sport = it },
            label = { Text("Sport") },
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
                onClick = {
                    onBackClick()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Text(text = "Cancel")
            }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = {
                    //action
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Text(text = "Opslaan")
            }
        }
        }
    }
}