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
            // Replace this with your actual LoginScreen composable function
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

        composable ("eventcreator") {
            EventCreateView(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}