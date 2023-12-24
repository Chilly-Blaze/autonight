package com.chillyblaze.autonight.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.chillyblaze.autonight.model.PersistentData
import com.chillyblaze.autonight.tools.ACTION_DATA_BROADCAST
import com.chillyblaze.autonight.tools.DEFAULT_PERSISTENT_DATA
import com.chillyblaze.autonight.tools.EnvState
import com.chillyblaze.autonight.tools.PersistentState.DAY
import com.chillyblaze.autonight.tools.PersistentState.ENABLE
import com.chillyblaze.autonight.tools.PersistentState.NIGHT
import com.chillyblaze.autonight.tools.grantedRoot
import com.chillyblaze.autonight.tools.rpc
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

    private fun context() = getApplication<Application>()

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            context().registerReceiver(
                receiver,
                IntentFilter(ACTION_DATA_BROADCAST),
                Context.RECEIVER_NOT_EXPORTED
            )
        else context().registerReceiver(receiver, IntentFilter(ACTION_DATA_BROADCAST))
    }

    fun bind() {
        registerReceiver()
        CoroutineScope(Dispatchers.IO).launch {
            if (grantedRoot()) {
                // init data
                withContext(Dispatchers.Main) { context().rpc { persistentData = state } }
                // update state
                ViewStateController.envCheck = EnvState.SUCCESS
            } else ViewStateController.envCheck = EnvState.ROOT_DENIED
        }
    }

    fun unbind() {
        context().unregisterReceiver(receiver)
    }

    fun submitThreshold(night: Int, day: Int) = context().rpc { setThreshold(night, day) }
    fun modeSwitch(enable: Boolean) = context().rpc { modeSwitch(enable) }

}