package fontys.ict.teamup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EventPager(pagerState: PagerState, modifier: Modifier = Modifier) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth()
    ) { page ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val screenHeight = maxHeight
            val dynamicSpacing = (screenHeight * 0.015f).coerceIn(8.dp, 20.dp)
            val horizontalAlignment = if (page == 0) Alignment.Start else Alignment.End

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
                if (page == 0) {
                    val joinedEvents = listOf(
                        "Maandag 25 september om 13:00 bij 'T Slotje",
                        "Donderdag 28 september om 15:30 bij de Donk",
                        "Maandag 2 oktober om 13:00 bij 'T Slotje",
                        "Donderdag 5 oktober om 15:30 bij de Donk",
                        "Maandag 9 oktober om 13:00 bij 'T Slotje",
                        "Donderdag 12 oktober om 15:30 bij de Donk",
                        "Maandag 16 oktober om 13:00 bij 'T Slotje",
                        "Donderdag 19 oktober om 15:30 bij de Donk"
                    )
                    joinedEvents.forEach { eventText ->
                        Card(modifier = Modifier.fillMaxWidth(0.60f)) {
                            Text(text = eventText, modifier = Modifier.padding(12.dp))
                        }
                    }
                } else {
                    val joinableEvents = listOf(
                        "Dinsdag 26 september om 19:00 bij 'T Slotje",
                        "Vrijdag 29 september om 18:00 bij de Donk",
                        "Dinsdag 3 oktober om 19:00 bij 'T Slotje",
                        "Vrijdag 6 oktober om 18:00 bij de Donk",
                        "Dinsdag 10 oktober om 19:00 bij 'T Slotje",
                        "Vrijdag 13 oktober om 18:00 bij de Donk",
                        "Dinsdag 17 oktober om 19:00 bij 'T Slotje",
                        "Vrijdag 20 oktober om 18:00 bij de Donk"
                    )
                    joinableEvents.forEach { eventText ->
                        Card(modifier = Modifier.fillMaxWidth(0.60f)) {
                            Text(text = eventText, modifier = Modifier.padding(12.dp))
                        }
                    }
                }
            }
        }
    }
}