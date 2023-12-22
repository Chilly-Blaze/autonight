package com.chillyblaze.autonight.tools

import com.chillyblaze.autonight.model.PersistentData

const val ACTION_DATA_BROADCAST = "com.chillyblaze.autonight.action.DATA_BROADCAST"
val DEFAULT_PERSISTENT_DATA = PersistentData(false, 50, 200)
const val SHARED_PREFERENCES_NAME = "data"

object PersistentState {
    const val ENABLE = "enable"
    const val NIGHT = "night"
    const val DAY = "day"
}

enum class EnvState {
    PENDING,
    SENSOR_MISSING,
    ROOT_DENIED,
    SUCCESS
}