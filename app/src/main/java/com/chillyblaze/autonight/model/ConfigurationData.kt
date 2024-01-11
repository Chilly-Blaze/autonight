package com.chillyblaze.autonight.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.edit
import com.chillyblaze.autonight.tools.ConfigurationState.DAY
import com.chillyblaze.autonight.tools.ConfigurationState.DELAY
import com.chillyblaze.autonight.tools.ConfigurationState.ENABLE
import com.chillyblaze.autonight.tools.ConfigurationState.NIGHT
import com.chillyblaze.autonight.tools.SHARED_PREFERENCES_NAME

// Threshold:                   night↘                       day↘
// Light:       Low<-____NIGHT_MODE__||___keep_current_status___||___DAY_MODE_____->High
data class ConfigurationData(
    var enable: Boolean, var night: Int, var day: Int, var delay: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (enable) 1 else 0)
        parcel.writeInt(night)
        parcel.writeInt(day)
        parcel.writeInt(delay)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ConfigurationData> {
        override fun createFromParcel(parcel: Parcel): ConfigurationData {
            return ConfigurationData(parcel)
        }

        override fun newArray(size: Int): Array<ConfigurationData?> {
            return arrayOfNulls(size)
        }
    }
}

fun Context.saveConfigurationData(data: ConfigurationData, sync: Boolean = false) {
    getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit {
        putBoolean(ENABLE, data.enable)
        putInt(NIGHT, data.night)
        putInt(DAY, data.day)
        putInt(DELAY, data.delay)
        if (sync) commit() else apply()
    }
}

fun Context.readConfigurationData(data: ConfigurationData): ConfigurationData {
    getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).apply {
        val enable = getBoolean(ENABLE, data.enable)
        val night = getInt(NIGHT, data.night)
        val day = getInt(DAY, data.day)
        val delay = getInt(DELAY, data.delay)
        return ConfigurationData(enable, night, day, delay)
    }
}