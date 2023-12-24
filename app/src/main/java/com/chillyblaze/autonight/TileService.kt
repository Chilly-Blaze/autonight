package com.chillyblaze.autonight

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_INACTIVE
import android.service.quicksettings.Tile.STATE_UNAVAILABLE
import android.service.quicksettings.TileService
import android.util.Log
import com.chillyblaze.autonight.tools.ACTION_DATA_BROADCAST
import com.chillyblaze.autonight.tools.PersistentState.ENABLE
import com.chillyblaze.autonight.tools.rpc
import com.topjohnwu.superuser.Shell

class TileService : TileService() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.apply {
                if (getBooleanExtra(ENABLE, qsTile.state == STATE_ACTIVE))
                    qsTile.state = STATE_ACTIVE
                else qsTile.state = STATE_INACTIVE
                qsTile.updateTile()
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            registerReceiver(receiver, IntentFilter(ACTION_DATA_BROADCAST), RECEIVER_NOT_EXPORTED)
        else registerReceiver(receiver, IntentFilter(ACTION_DATA_BROADCAST))
    }

    override fun onStartListening() {
        super.onStartListening()
        if (Shell.isAppGrantedRoot() == false) qsTile.state = STATE_UNAVAILABLE
        else rpc { qsTile.state = if (state.enable) STATE_ACTIVE else STATE_INACTIVE }
        Log.d("TileService", Shell.isAppGrantedRoot().toString())
        qsTile.updateTile()
    }

    override fun onClick() {
        when (qsTile.state) {
            STATE_INACTIVE -> rpc { modeSwitch(true) }
            STATE_ACTIVE -> rpc { modeSwitch(false) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}