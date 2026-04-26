package com.ayforge.tattoomasterapp.core.utils

import android.os.Build

object DeviceHelper {

    fun isXiaomi(): Boolean {
        return Build.MANUFACTURER.lowercase().contains("xiaomi")
    }

    fun isHuawei(): Boolean {
        return Build.MANUFACTURER.lowercase().contains("huawei")
    }

    fun isOppo(): Boolean {
        return Build.MANUFACTURER.lowercase().contains("oppo")
    }
}