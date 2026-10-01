@file:Suppress("SpellCheckingInspection")

package fontys.ict.teamup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fontys.ict.teamup.ui.theme.TeamUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            TeamUpTheme {
                TeamUpApp()
            }
        }
    }
}

@Composable
fun TeamUpApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "events") {
        composable("events") {
            `Event-view`(
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }

        composable("settings") {
            SettingsView(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun `Event-view`(onSettingsClick: () -> Unit) {
    val topInsetPadding = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = topInsetPadding + 8.dp,
                start = 8.dp,
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
                    text = "Willem welkom",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "Filters: [50km] [Basketbal]",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Geplande evenementen:",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Settings",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        onSettingsClick()
                    }
                    .size(32.dp),
                tint = Color.Black
            )
        }
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val screenHeight = maxHeight
            val dynamicSpacing = (screenHeight * 0.015f).coerceIn(8.dp, 20.dp)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = screenHeight)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(
                    dynamicSpacing,
                    Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.Start
            ) {
                val eventText =
                    "Er staat een basketbal evenement gepland op Maandag 24 Sepetember om 9:00 bij basketbalveld 'T Slotje"

                repeat(8) {
                    Card(modifier = Modifier.fillMaxWidth(0.60f)) {
                        Text(text = eventText, modifier = Modifier.padding(12.dp))
                    }
                }
            }
        }
    }
}