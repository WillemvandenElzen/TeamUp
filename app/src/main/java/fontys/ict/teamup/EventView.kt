package fontys.ict.teamup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun `Event-view`(
    onSettingsClick: () -> Unit,
    onEventCreateClick: () -> Unit
) {
    val topInsetPadding = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()
    val pagerState = rememberPagerState(pageCount = { 2 })

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topInsetPadding + 8.dp,
                    start = 16.dp,
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

                    val sectionTitle = if (pagerState.currentPage == 0) {
                        "Ingeschreven evenementen:"
                    } else {
                        "Beschikbare evenementen:"
                    }

                    Text(
                        text = sectionTitle,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onSettingsClick() }
                        .size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            EventPager(
                pagerState = pagerState,
                modifier = Modifier.weight(1f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Maak een evenement aan!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onEventCreateClick() }
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(2) { index ->
                        val isSelected = pagerState.currentPage == index
                        val dotWidth = if (isSelected) 24.dp else 8.dp
                        val dotColor =
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray

                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(dotWidth)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                    }
                }
            }
        }
    }
}