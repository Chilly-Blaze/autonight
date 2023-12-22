package com.chillyblaze.autonight.tools

import com.topjohnwu.superuser.Shell


fun hasNull(vararg args: Any?) = args.filterNotNull().size != args.size
fun grantedRoot() = run { Shell.getShell(); Shell.isAppGrantedRoot() } ?: false