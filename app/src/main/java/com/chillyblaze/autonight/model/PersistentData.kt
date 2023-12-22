package com.chillyblaze.autonight.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.edit
import com.chillyblaze.autonight.tools.PersistentState.DAY
import com.chillyblaze.autonight.tools.PersistentState.ENABLE
import com.chillyblaze.autonight.tools.PersistentState.NIGHT
import com.chillyblaze.autonight.tools.SHARED_PREFERENCES_NAME

// Threshold:                   night↘                    day↘
// Light:       Low<-____NIGHT_MODE__||___keep_current_status___||___DAY_MODE_____->High
data class PersistentData(var enable: Boolean, var night: Int, var day: Int) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (enable) 1 else 0)
        parcel.writeInt(night)
        parcel.writeInt(day)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PersistentData> {
        override fun createFromParcel(parcel: Parcel): PersistentData {
            return PersistentData(parcel)
        }

        override fun newArray(size: Int): Array<PersistentData?> {
            return arrayOfNulls(size)
        }
    }
}

fun Context.savePersistentData(data: PersistentData, sync: Boolean = false) {
    getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit {
        putBoolean(ENABLE, data.enable)
        putInt(NIGHT, data.night)
        putInt(DAY, data.day)
        if (sync) commit() else apply()
    }
}

fun Context.readPersistentData(data: PersistentData): PersistentData {
    getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).apply {
        val enable = getBoolean(ENABLE, data.enable)
        val night = getInt(NIGHT, data.night)
        val day = getInt(DAY, data.day)
        return PersistentData(enable, night, day)
    }
}