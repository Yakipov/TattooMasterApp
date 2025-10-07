package com.ayforge.tattoomasterapp.core.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

class LanguageManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LANGUAGE = "language"
    }

    fun getCurrentLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, Locale.getDefault().language) ?: "en"
    }

    fun setLanguage(language: String) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
        updateResources(language)
    }

    fun toggleLanguage(): String {
        val newLang = if (getCurrentLanguage() == "en") "ru" else "en"
        setLanguage(newLang)
        return newLang
    }

    /** Применяем язык к Context */
    private fun updateResources(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
