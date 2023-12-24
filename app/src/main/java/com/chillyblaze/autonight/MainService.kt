package com.chillyblaze.autonight

import android.app.UiModeManager
import android.app.UiModeManager.MODE_NIGHT_AUTO
import android.app.UiModeManager.MODE_NIGHT_CUSTOM
import android.app.UiModeManager.MODE_NIGHT_NO
import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.core.content.getSystemService
import com.chillyblaze.autonight.model.PersistentData
import com.chillyblaze.autonight.model.readPersistentData
import com.chillyblaze.autonight.model.savePersistentData
import com.chillyblaze.autonight.tools.ACTION_DATA_BROADCAST
import com.chillyblaze.autonight.tools.DEFAULT_PERSISTENT_DATA
import com.chillyblaze.autonight.tools.PersistentState.DAY
import com.chillyblaze.autonight.tools.PersistentState.ENABLE
import com.chillyblaze.autonight.tools.PersistentState.NIGHT
import com.topjohnwu.superuser.ipc.RootService

class MainService : RootService(), SensorEventListener {

    private lateinit var modeManager: UiModeManager
    private lateinit var sensorManager: SensorManager
    private var persistentData = DEFAULT_PERSISTENT_DATA
    private val autoNightService = object : IAutoNightService.Stub() {
        override fun modeSwitch(enable: Boolean) {
            if (enable) registerListener() else unregisterListener()
            persistentData.enable = enable
            sendStateBroadcast()
        }

        override fun setThreshold(night: Int, day: Int) {
            persistentData.night = night
            persistentData.day = day
            sendStateBroadcast()
        }

        override fun getState(): PersistentData = persistentData

    }

    private fun sendStateBroadcast() {
        savePersistentData(persistentData, true)
        Intent(ACTION_DATA_BROADCAST).apply {
            putExtra(ENABLE, persistentData.enable)
            putExtra(NIGHT, persistentData.night)
            putExtra(DAY, persistentData.day)
            sendBroadcast(this)
        }
    }

    private fun registerListener() = sensorManager.registerListener(
        this,
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
        SensorManager.SENSOR_DELAY_NORMAL
    )

    private fun unregisterListener() = sensorManager.unregisterListener(this@MainService)

    override fun onCreate() {
        super.onCreate()
        persistentData = readPersistentData(persistentData)
        sensorManager = getSystemService<SensorManager>()!!
        modeManager = getSystemService<UiModeManager>()!!
    }

    override fun onBind(intent: Intent): IBinder {
        if (persistentData.enable) registerListener()
        return autoNightService
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.get(0)?.let { light ->
            when (modeManager.nightMode) {
                MODE_NIGHT_NO -> MODE_NIGHT_YES.takeIf { light < persistentData.night }
                MODE_NIGHT_YES -> MODE_NIGHT_NO.takeIf { light > persistentData.day }
                MODE_NIGHT_AUTO, MODE_NIGHT_CUSTOM ->
                    MODE_NIGHT_YES.takeIf { light > persistentData.night } ?: MODE_NIGHT_NO

                else -> null
            }?.let(modeManager::setNightMode)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        savePersistentData(persistentData)
        super.onDestroy()
    }
}