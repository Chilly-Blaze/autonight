package com.chillyblaze.autonight.tools

import com.chillyblaze.autonight.model.ConfigurationData

const val ACTION_DATA_BROADCAST = "com.chillyblaze.autonight.action.DATA_BROADCAST"
val DEFAULT_CONFIGURATION_DATA = ConfigurationData(false, 50, 200,5)
const val SHARED_PREFERENCES_NAME = "data"

object ConfigurationState {
    const val ENABLE = "enable"
    const val NIGHT = "night"
    const val DAY = "day"
    const val DELAY = "delay"
}

enum class EnvState {
    PENDING,
    SENSOR_MISSING,
    ROOT_DENIED,
    SUCCESS
}