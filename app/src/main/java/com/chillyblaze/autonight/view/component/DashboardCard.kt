package com.chillyblaze.autonight.view.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chillyblaze.autonight.R
import com.chillyblaze.autonight.viewmodel.RemoteController
import com.chillyblaze.autonight.viewmodel.ViewStateController.DelayAnimatedVisibility

sealed class Switch(val title: Int) {
    val container: Pair<Color, Color>
        @Composable
        @ReadOnlyComposable
        get() = if (this is Enable) colorScheme.secondaryContainer to colorScheme.primaryContainer
        else colorScheme.surface to colorScheme.surface
    val content: Pair<Color, Color>
        @Composable
        @ReadOnlyComposable
        get() = if (this is Enable) colorScheme.onSecondaryContainer to colorScheme.onPrimaryContainer
        else colorScheme.onSurface to colorScheme.onSurface
    val icon: Pair<Int, Color>
        @Composable
        @ReadOnlyComposable
        get() = if (this is Enable) R.drawable.ic_play to colorScheme.tertiary
        else R.drawable.ic_play_disabled to colorScheme.onSurface

    data object Enable : Switch(R.string.dashboard_enable_title)
    data object Disable : Switch(R.string.dashboard_disable_tile)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardCard(delay: Int) {
    DelayAnimatedVisibility(time = delay) {
        val remoteController = viewModel<RemoteController>()
        val switch =
            if (remoteController.configurationData.enable) Switch.Enable else Switch.Disable
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 5.dp),
            colors = CardDefaults.cardColors(switch.container.first, switch.content.first),
            onClick = { remoteController.modeSwitch(switch != Switch.Enable) }
        ) {
            Row(Modifier.padding(15.dp, 5.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    painter = painterResource(id = switch.icon.first),
                    contentDescription = null,
                    tint = switch.icon.second
                )
                Column(Modifier.padding(10.dp)) {
                    Text(text = stringResource(id = switch.title), style = typography.titleMedium)
                    Spacer(modifier = Modifier.height(5.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            switch.container.second,
                            switch.content.second
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.dashboard_threshold_content) +
                                    "${remoteController.configurationData.night}/" +
                                    "${remoteController.configurationData.day}/" +
                                    "${remoteController.configurationData.delay}",
                            style = typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(10.dp, 2.dp)
                        )
                    }
                }
            }
        }
    }
}