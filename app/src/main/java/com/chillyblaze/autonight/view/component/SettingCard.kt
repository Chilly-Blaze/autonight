package com.chillyblaze.autonight.view.component

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chillyblaze.autonight.R
import com.chillyblaze.autonight.viewmodel.RemoteController
import com.chillyblaze.autonight.viewmodel.SensorController
import com.chillyblaze.autonight.viewmodel.ViewStateController.DelayAnimatedVisibility
import com.chillyblaze.autonight.viewmodel.ViewStateController.checkNSubmit
import com.chillyblaze.autonight.viewmodel.ViewStateController.settingDay
import com.chillyblaze.autonight.viewmodel.ViewStateController.settingDelay
import com.chillyblaze.autonight.viewmodel.ViewStateController.settingNight

sealed class ConfigurationContent(
    val label: Int,
    val hint: Int,
    private val stateGet: () -> String,
    private val stateSet: (String) -> Unit
) {
    var state: String
        get() = stateGet()
        set(value) = stateSet(value)

    data object A : ConfigurationContent(
        R.string.setting_night,
        R.string.setting_night_hint,
        ::settingNight::get,
        ::settingNight::set
    )

    data object B : ConfigurationContent(
        R.string.setting_day,
        R.string.setting_day_hint,
        ::settingDay::get,
        ::settingDay::set
    )

    data object C : ConfigurationContent(
        R.string.setting_delay,
        R.string.setting_delay_hint,
        ::settingDelay::get,
        ::settingDelay::set
    )
}

@Composable
fun SettingCard(delay: Int) {
    DelayAnimatedVisibility(time = delay) {
        val remoteController: RemoteController = viewModel()
        val toast = Toast(LocalContext.current).apply { duration = Toast.LENGTH_LONG }
        ElevatedCard(
            colors = CardDefaults.cardColors(colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 5.dp)
        ) {
            Row(
                Modifier.padding(15.dp, 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_setting),
                            contentDescription = null,
                            tint = colorScheme.tertiary
                        )
                        Text(
                            text = stringResource(id = R.string.setting_title),
                            modifier = Modifier.padding(10.dp, 0.dp),
                            style = typography.titleMedium
                        )
                    }
                    ConfigurationContent::class.sealedSubclasses.forEach { InputBox(it.objectInstance!!) }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CurrentBox()
                    Spacer(modifier = Modifier.height(5.dp))
                    Button(onClick = { checkNSubmit(remoteController, toast) }) {
                        Text(text = stringResource(id = R.string.setting_submit_button))
                    }
                }
            }
        }
    }
}

@Composable
private fun InputBox(content: ConfigurationContent) {
    OutlinedTextField(
        value = content.state,
        textStyle = typography.bodyMedium,
        onValueChange = content::state::set,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(text = stringResource(id = content.label)) },
        singleLine = true,
        supportingText = {
            Text(text = stringResource(id = content.hint), color = colorScheme.primary)
        },
        modifier = Modifier.fillMaxWidth(0.6f)
    )
}

@Composable
private fun CurrentBox() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.setting_current_hint),
            style = typography.bodyMedium
        )
        Card(
            colors = CardDefaults.cardColors(
                colorScheme.primaryContainer,
                colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.padding(5.dp)
        ) {
            Text(
                text = SensorController.currentLight.toInt().toString(),
                modifier = Modifier.padding(10.dp, 5.dp),
                style = typography.titleLarge
            )
        }
    }
}