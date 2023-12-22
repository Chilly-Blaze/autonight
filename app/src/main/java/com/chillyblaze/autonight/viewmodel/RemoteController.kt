package com.chillyblaze.autonight.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.chillyblaze.autonight.IAutoNightService
import com.chillyblaze.autonight.MainService
import com.chillyblaze.autonight.model.PersistentData
import com.chillyblaze.autonight.tools.ACTION_DATA_BROADCAST
import com.chillyblaze.autonight.tools.DEFAULT_PERSISTENT_DATA
import com.chillyblaze.autonight.tools.EnvState
import com.chillyblaze.autonight.tools.PersistentState.DAY
import com.chillyblaze.autonight.tools.PersistentState.ENABLE
import com.chillyblaze.autonight.tools.PersistentState.NIGHT
import com.chillyblaze.autonight.tools.grantedRoot
import com.topjohnwu.superuser.ipc.RootService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RemoteController(application: Application) : AndroidViewModel(application) {

    var persistentData by mutableStateOf(DEFAULT_PERSISTENT_DATA)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.apply {
                persistentData = PersistentData(
                    getBooleanExtra(ENABLE, persistentData.enable),
                    getIntExtra(NIGHT, persistentData.night),
                    getIntExtra(DAY, persistentData.day)
                )
            }
        }
    }

    private fun rpc(callback: IAutoNightService.() -> Unit) {
        Intent(getApplication(), MainService::class.java).apply {
            addCategory(RootService.CATEGORY_DAEMON_MODE)
            RootService.bind(this, object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) =
                    IAutoNightService.Stub.asInterface(service).callback()

                override fun onServiceDisconnected(name: ComponentName?) {}
            })
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getApplication<Application>().registerReceiver(
                receiver,
                IntentFilter(ACTION_DATA_BROADCAST),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            getApplication<Application>().registerReceiver(
                receiver,
                IntentFilter(ACTION_DATA_BROADCAST)
            )
        }
    }

    fun bind() {
        registerReceiver()
        CoroutineScope(Dispatchers.IO).launch {
            if (grantedRoot()) {
                // init data
                withContext(Dispatchers.Main) { rpc { persistentData = state } }
                // update state
                ViewStateController.envCheck = EnvState.SUCCESS
            } else ViewStateController.envCheck = EnvState.ROOT_DENIED
        }
    }

    fun unbind() {
        getApplication<Application>().unregisterReceiver(receiver)
    }

    fun submitThreshold(night: Int, day: Int) = rpc { setThreshold(night, day) }
    fun modeSwitch(enable: Boolean) = rpc { modeSwitch(enable) }

}