package com.chillyblaze.autonight

import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.content.getSystemService
import com.chillyblaze.autonight.view.MainLayout
import com.chillyblaze.autonight.viewmodel.RemoteController
import com.chillyblaze.autonight.viewmodel.SensorController


class MainActivity : ComponentActivity() {
    private val remoteController: RemoteController by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SensorController.bind(getSystemService<SensorManager>())
        remoteController.bind()
        setContent { MainLayout() }
    }

    override fun onResume() {
        super.onResume()
        SensorController.registerListener()
    }

    override fun onPause() {
        super.onPause()
        SensorController.unregisterListener()
    }

    override fun onDestroy() {
        remoteController.unbind()
        super.onDestroy()
    }
}