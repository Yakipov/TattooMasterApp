package com.ayforge.tattoomasterapp.core.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("tattoo_prefs", Context.MODE_PRIVATE)

    var isUserSignedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_SIGNED_IN, false)
        set(value) {
            prefs.edit().putBoolean(KEY_IS_SIGNED_IN, value).apply()
        }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_IS_SIGNED_IN = "is_signed_in"
    }
}
