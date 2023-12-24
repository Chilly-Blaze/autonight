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
    var cardList = mutableStateMapOf<Int, Boolean>()

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