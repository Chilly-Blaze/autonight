package com.chillyblaze.autonight.viewmodel

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.chillyblaze.autonight.R
import com.chillyblaze.autonight.tools.EnvState
import com.chillyblaze.autonight.tools.hasNull
import kotlinx.coroutines.delay

object ViewStateController {
    var envCheck: EnvState by mutableStateOf(EnvState.PENDING)
    var hintSuccessShow by mutableStateOf(true)
    var settingDay by mutableStateOf("")
    var settingNight by mutableStateOf("")
    var settingDelay by mutableStateOf("")
    var cardList = mutableStateMapOf<Int, Boolean>()

    fun checkNSubmit(remote: RemoteController, toast: Toast) {
        remote.apply {
            fun String.check(default: Int) =
                if (isBlank()) default
                else if ((toUIntOrNull()?.toInt() ?: -1) < 0)
                    toast.setText(R.string.setting_value_error2).let { null }
                else this.toInt()

            fun submit(night: Int, day: Int, delay: Int) {
                "".also(::settingDay::set).also(::settingNight::set).also(::settingDelay::set)
                submitSettings(night, day, delay)
            }

            val night = settingNight.check(configurationData.night)
            val day = settingDay.check(configurationData.day)
            val delay = settingDelay.check(configurationData.delay)
            if (hasNull(night, day, delay) ||
                (night!! > day!!).also { toast.setText(R.string.setting_value_error1) }
            ) toast.show()
            else submit(night, day, delay!!)
        }
    }

    @Composable
    fun DelayAnimatedVisibility(
        time: Int,
        content: @Composable AnimatedVisibilityScope.() -> Unit
    ) {
        cardList.putIfAbsent(time, false)
        LaunchedEffect(envCheck == EnvState.SUCCESS) {
            delay(time.toLong())
            cardList[time] = envCheck == EnvState.SUCCESS
        }
        AnimatedVisibility(visible = cardList[time]!!, content = content)
    }
}