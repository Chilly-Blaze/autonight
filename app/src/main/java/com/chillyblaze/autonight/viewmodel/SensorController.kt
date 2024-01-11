package com.chillyblaze.autonight.viewmodel

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import com.chillyblaze.autonight.tools.EnvState
import com.chillyblaze.autonight.tools.hasNull

object SensorController : SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var lightSensor: Sensor? = null

    fun bind(manager: SensorManager?) {
        sensorManager = manager
        lightSensor = manager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (hasNull(sensorManager, lightSensor)) {
            ViewStateController.envCheck = EnvState.SENSOR_MISSING
        }
    }

    var currentLight by mutableFloatStateOf(0f)

    fun registerListener() = sensorManager?.registerListener(
        this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL
    ) ?: false

    fun unregisterListener() = sensorManager?.unregisterListener(this) ?: false

    override fun onSensorChanged(event: SensorEvent?) {
        currentLight = event?.values?.get(0) ?: -1f
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}