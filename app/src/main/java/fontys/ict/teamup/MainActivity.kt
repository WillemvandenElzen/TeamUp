@file:Suppress("SpellCheckingInspection")

package fontys.ict.teamup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fontys.ict.teamup.ui.theme.TeamUpTheme
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "http://10.61.0.42:8000",
    supabaseKey = "sb_publishable_dgy9B6ZLa4QctV0fuesptz_XSnHBS6E"
) {
    install(Postgrest)
    install(Auth)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            var themeMode by remember { mutableStateOf("System") }

            val darkTheme = when (themeMode) {
                "Light" -> false
                "Dark" -> true
                else -> isSystemInDarkTheme()
            }

            TeamUpTheme(darkTheme = darkTheme) {
                TeamUpApp(
                    currentThemeMode = themeMode,
                    onThemeModeChanged = { newMode -> themeMode = newMode }
                )
            }
        }
    }
}

@Composable
fun TeamUpApp(
    currentThemeMode: String,
    onThemeModeChanged: (String) -> Unit
) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val session = try {
            supabase.auth.currentSessionOrNull()
        } catch (_: Exception) {
            null
        }
        startDestination = if (session != null) "events" else "login"
    }

    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(navController = navController, startDestination = startDestination!!) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("events") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("events") {
                LaunchedEffect(Unit) {
                    try {
                        checkAndProcessEventsAndElo()
                    } catch (_: Exception) {
                    }
                }

                `Event-view`(
                    onSettingsClick = {
                        navController.navigate("settings")
                    },
                    onEventCreateClick = {
                        navController.navigate("eventcreator")
                    },
                    onLeaderboardClick = {
                        navController.navigate("leaderboard")
                    },
                    onEventClick = { eventId ->
                        navController.navigate("event_detail/$eventId")
                    }
                )
            }

            composable("leaderboard") {
                LeaderboardView(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable("settings") {
                SettingsView(
                    currentThemeMode = currentThemeMode,
                    onThemeModeChanged = onThemeModeChanged,
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable("eventcreator") {
                EventCreateView(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable("event_detail/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                EventDetailView(
                    eventId = eventId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}