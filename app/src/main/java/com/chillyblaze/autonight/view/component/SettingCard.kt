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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chillyblaze.autonight.R
import com.chillyblaze.autonight.viewmodel.RemoteController
import com.chillyblaze.autonight.viewmodel.SensorController
import com.chillyblaze.autonight.viewmodel.ViewStateController
import com.chillyblaze.autonight.viewmodel.ViewStateController.DelayAnimatedVisibility

@Composable
fun SettingCard(delay:Int) {
    DelayAnimatedVisibility(time = delay) {
        val remoteController: RemoteController = viewModel()
        val toast = Toast.makeText(
            remoteController.getApplication(),
            stringResource(id = R.string.setting_value_error),
            Toast.LENGTH_LONG
        )
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
                    InputBox(
                        state = ViewStateController.settingNight,
                        onChange = { ViewStateController.settingNight = it },
                        content = DayNight.Night
                    )
                    InputBox(
                        state = ViewStateController.settingDay,
                        onChange = { ViewStateController.settingDay = it },
                        content = DayNight.Day
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CurrentBox()
                    Spacer(modifier = Modifier.height(5.dp))
                    Button(onClick = {
                        remoteController.apply {
                            val night = ViewStateController.settingNight.toIntOrNull()
                                ?: persistentData.night
                            val day = ViewStateController.settingDay.toIntOrNull()
                                ?: persistentData.day
                            if (night > day) toast.show()
                            else if (night != persistentData.night || day != persistentData.day) {
                                ViewStateController.settingNight = ""
                                ViewStateController.settingDay = ""
                                submitThreshold(night, day)
                            }
                        }
                    }) {
                        Text(text = stringResource(id = R.string.setting_submit_button))
                    }
                }
            }
        }
    }
}

sealed class DayNight(val label: Int, val hint: Int) {
    data object Day : DayNight(R.string.setting_day, R.string.setting_day_hint)
    data object Night : DayNight(R.string.setting_night, R.string.setting_night_hint)
}

@Composable
private fun InputBox(state: String, onChange: (String) -> Unit, content: DayNight) {
    OutlinedTextField(
        value = state,
        textStyle = typography.bodyMedium,
        onValueChange = onChange,
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