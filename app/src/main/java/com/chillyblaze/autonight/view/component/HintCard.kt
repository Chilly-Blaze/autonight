package com.chillyblaze.autonight.view.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chillyblaze.autonight.R
import com.chillyblaze.autonight.tools.EnvState
import com.chillyblaze.autonight.viewmodel.ViewStateController

@Composable
fun HintCard() {
    AnimatedContent(targetState = ViewStateController.envCheck, label = "") {
        when (it) {
            EnvState.PENDING -> PendingCard()
            EnvState.SENSOR_MISSING -> ErrorCard(content = R.string.hint_sensor_missing_content)
            EnvState.ROOT_DENIED -> ErrorCard(content = R.string.hint_root_denied_content)
            EnvState.SUCCESS -> AnimatedVisibility(visible = ViewStateController.hintSuccessShow) { SuccessCard() }
        }
    }
}

@Composable
private fun PendingCard() {
    val colors = Colors(
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.secondary
    )
    CardTemplate(
        colors = colors,
        icon = R.drawable.ic_pending,
        title = R.string.hint_pending_title
    )
}

@Composable
private fun ErrorCard(content: Int) {
    val colors = Colors(
        MaterialTheme.colorScheme.errorContainer,
        MaterialTheme.colorScheme.onErrorContainer,
        MaterialTheme.colorScheme.error
    )
    CardTemplate(
        colors = colors,
        icon = R.drawable.ic_error,
        title = R.string.hint_error_title,
        content = content
    )
}

@Composable
private fun SuccessCard() {
    val colors = Colors(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.primary
    )
    CardTemplate(
        colors = colors,
        icon = R.drawable.ic_check,
        title = R.string.hint_success_title,
        onClick = { ViewStateController.hintSuccessShow = false }
    )
}

typealias Colors = Triple<Color, Color, Color>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardTemplate(
    colors: Colors,
    icon: Int,
    title: Int,
    content: Int? = null,
    onClick: () -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.first,
            contentColor = colors.second
        ),
        onClick = onClick
    ) {
        Column(Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = colors.third
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(id = title),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            content?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = stringResource(id = it), style = MaterialTheme.typography.bodyMedium)
            }
        }

    }
}