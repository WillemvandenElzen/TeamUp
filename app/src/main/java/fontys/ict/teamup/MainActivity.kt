@file:Suppress("SpellCheckingInspection")

package fontys.ict.teamup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fontys.ict.teamup.ui.theme.TeamUpTheme
import io.github.jan.supabase.auth.Auth
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
            TeamUpTheme {
                TeamUpApp()
            }
        }
    }
}

@Composable
fun TeamUpApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
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
            `Event-view`(
                onSettingsClick = {
                    navController.navigate("settings")
                },
                onEventCreateClick = {
                    navController.navigate("eventcreator")
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

        composable("eventcreator") {
            EventCreateView(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}