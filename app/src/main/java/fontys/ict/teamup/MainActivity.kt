package fontys.ict.teamup

import android.media.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.unit.dp
import fontys.ict.teamup.ui.theme.Purple40
import fontys.ict.teamup.ui.theme.TeamUpTheme
import org.intellij.lang.annotations.JdkConstants

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TeamUpTheme {
                Table(name = "Android")
            }
        }
    }
}

@Composable
fun Table(name: String, modifier: Modifier = Modifier) {
    var selectedPerson by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Gabriel",
                modifier = Modifier
                    .background(color = if (selectedPerson == "Gabriel") Color.Red else Color.LightGray)
                    .weight(1f)
                    .clickable {
                        if (selectedPerson == "Gabriel") {
                            selectedPerson = null
                        } else {
                            selectedPerson = "Gabriel"
                        }
                    })

            Text(text = "Remco",
                modifier = Modifier
                    .background(color = if (selectedPerson == "Remco") Color.Yellow else Color.Blue)
                    .weight(1f)
                    .clickable {
                        if (selectedPerson == "Remco") {
                            selectedPerson = null
                        } else {
                            selectedPerson = "Remco"
                        }
                    }
                    )

            Text(text = "Jason-Dean",
                modifier = Modifier
                    .background(color = Color.Blue)
                    .weight(1f))
        }
        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Npc",
                modifier = Modifier
                    .background(color = Color.Green)
                    .weight(1f))

            Text(text = "Willem",
                modifier = Modifier
                    .background(color = Color.Cyan)
                    .weight(1f))

            Text(text = "Falco",
                modifier = Modifier
                    .background(color = Color.Gray)
                    .weight(1f))
        }
    }
}