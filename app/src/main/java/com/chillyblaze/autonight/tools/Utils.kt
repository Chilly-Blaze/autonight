package com.chillyblaze.autonight.tools

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.chillyblaze.autonight.IAutoNightService
import com.chillyblaze.autonight.MainService
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService


fun hasNull(vararg args: Any?) = args.filterNotNull().size != args.size
fun grantedRoot() = run { Shell.getShell(); Shell.isAppGrantedRoot() } ?: false
fun Context.rpc(callback: IAutoNightService.() -> Unit) {
    Intent(this, MainService::class.java).apply {
        addCategory(RootService.CATEGORY_DAEMON_MODE)
        RootService.bind(this, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) =
                IAutoNightService.Stub.asInterface(service).callback()

            override fun onServiceDisconnected(name: ComponentName?) {}
        })
    }
}