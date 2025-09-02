package com.ayforge.tattoomasterapp.core.settings

import android.content.Context
import android.content.res.Configuration
import java.util.*

class LanguageManager(
    private val context: Context,
    private val settingsDataStore: SettingsDataStore
) {

    suspend fun setLanguage(languageCode: String) {
        settingsDataStore.setLanguage(languageCode)
        updateResources(languageCode)
    }


    suspend fun getCurrentLanguage(): String {
        return settingsDataStore.getLanguage()
    }

    fun updateResources(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
