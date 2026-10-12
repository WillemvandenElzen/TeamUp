package fontys.ict.teamup

import io.github.jan.supabase.postgrest.postgrest

/**
 * Roept de server-side PostgreSQL functie aan om inactiviteit (elo-decay)
 * en evenementen/kapitein-timers af te handelen.
 */
suspend fun checkAndProcessEventsAndElo() {
    try {
        supabase.postgrest.rpc("process_inactivity_and_events")
    } catch (_: Exception) {
    }
}