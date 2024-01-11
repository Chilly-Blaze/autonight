package com.chillyblaze.autonight

import android.app.KeyguardManager
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
import android.os.PowerManager
import androidx.core.content.getSystemService
import com.chillyblaze.autonight.model.ConfigurationData
import com.chillyblaze.autonight.model.readConfigurationData
import com.chillyblaze.autonight.model.saveConfigurationData
import com.chillyblaze.autonight.tools.ACTION_DATA_BROADCAST
import com.chillyblaze.autonight.tools.ConfigurationState.DAY
import com.chillyblaze.autonight.tools.ConfigurationState.DELAY
import com.chillyblaze.autonight.tools.ConfigurationState.ENABLE
import com.chillyblaze.autonight.tools.ConfigurationState.NIGHT
import com.chillyblaze.autonight.tools.DEFAULT_CONFIGURATION_DATA
import com.topjohnwu.superuser.ipc.RootService

class MainService : RootService(), SensorEventListener {

    private lateinit var modeManager: UiModeManager
    private lateinit var sensorManager: SensorManager
    private lateinit var powerManager: PowerManager
    private lateinit var keyguardManager: KeyguardManager
    private var configurationData = DEFAULT_CONFIGURATION_DATA
    private var delayCounts = configurationData.delay
    private val autoNightService = object : IAutoNightService.Stub() {
        override fun modeSwitch(enable: Boolean) {
            if (enable) registerListener() else unregisterListener()
            configurationData.enable = enable
            sendStateBroadcast()
        }

        override fun setThreshold(night: Int, day: Int) {
            configurationData.night = night
            configurationData.day = day
            sendStateBroadcast()
        }

        override fun setDelay(delay: Int) {
            configurationData.delay = delay
            delayCounts = delay
            sendStateBroadcast()
        }

        override fun getState(): ConfigurationData = configurationData

    }

    private fun sendStateBroadcast() {
        saveConfigurationData(configurationData, true)
        Intent(ACTION_DATA_BROADCAST).apply {
            putExtra(ENABLE, configurationData.enable)
            putExtra(NIGHT, configurationData.night)
            putExtra(DAY, configurationData.day)
            putExtra(DELAY, configurationData.delay)
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
        configurationData = readConfigurationData(configurationData)
        delayCounts = configurationData.delay
        sensorManager = getSystemService<SensorManager>()!!
        modeManager = getSystemService<UiModeManager>()!!
        powerManager = getSystemService<PowerManager>()!!
        keyguardManager = getSystemService<KeyguardManager>()!!
    }

    override fun onBind(intent: Intent): IBinder {
        if (configurationData.enable) registerListener()
        return autoNightService
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.takeIf {
            powerManager.isInteractive && !keyguardManager.isKeyguardLocked
        }?.get(0)?.let { light ->
            fun Int?.delayJudge(willing: Boolean) = takeIf {
                (if (willing) delayCounts - 1 else configurationData.delay).also(::delayCounts::set) == -1
            }
            when (modeManager.nightMode) {
                MODE_NIGHT_NO -> MODE_NIGHT_YES.delayJudge(light < configurationData.night)
                MODE_NIGHT_YES -> MODE_NIGHT_NO.delayJudge(light > configurationData.day)
                MODE_NIGHT_AUTO, MODE_NIGHT_CUSTOM -> if (light > configurationData.night) MODE_NIGHT_YES else MODE_NIGHT_NO
                else -> null
            }?.let(modeManager::setNightMode)?.also { delayCounts = configurationData.delay }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        saveConfigurationData(configurationData)
        super.onDestroy()
    }
}