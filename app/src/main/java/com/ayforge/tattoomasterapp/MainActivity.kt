package com.ayforge.tattoomasterapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.ayforge.tattoomasterapp.core.settings.LanguageManager
import com.ayforge.tattoomasterapp.presentation.navigation.AppNavGraph
import com.ayforge.tattoomasterapp.ui.theme.TattooMasterAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var languageManager: LanguageManager

    override fun attachBaseContext(newBase: Context) {
        // Передаём контекст с выбранным языком
        val manager = LanguageManager(newBase)
        val lang = manager.getCurrentLanguage()
        val context = newBase.apply {
            val locale = java.util.Locale(lang)
            java.util.Locale.setDefault(locale)

            val config = resources.configuration
            config.setLocale(locale)
            applyOverrideConfiguration(config)
        }
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageManager = LanguageManager(this)
        val sessionManager = SessionManager(this)

        setContent {
            TattooMasterAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavGraph(
                        navController = navController,
                        sessionManager = sessionManager
                    )
                }
            }
        }
    }
}
