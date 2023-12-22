package com.chillyblaze.autonight.viewmodel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.chillyblaze.autonight.tools.EnvState
import kotlinx.coroutines.delay

object ViewStateController {
    var envCheck: EnvState by mutableStateOf(EnvState.PENDING)
    var hintSuccessShow by mutableStateOf(true)
    var settingDay by mutableStateOf("")
    var settingNight by mutableStateOf("")
    private var cardList = mutableStateMapOf<Long, Boolean>()

    @Composable
    fun DelayAnimatedVisibility(
        time: Long,
        content: @Composable AnimatedVisibilityScope.() -> Unit
    ) {
        cardList.putIfAbsent(time, false)
        LaunchedEffect(envCheck == EnvState.SUCCESS) {
            delay(time)
            cardList[time] = envCheck == EnvState.SUCCESS
        }
        AnimatedVisibility(visible = cardList[time]!!, content = content)
    }
}