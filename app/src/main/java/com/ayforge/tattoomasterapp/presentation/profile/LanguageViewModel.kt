package com.ayforge.tattoomasterapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayforge.tattoomasterapp.core.settings.LanguageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class LanguageViewModel(
    private val languageManager: LanguageManager
) : ViewModel() {

    private val _currentLanguage = MutableStateFlow("en") // Значение по умолчанию
    val currentLanguage: StateFlow<String> = _currentLanguage

    init {
        loadCurrentLanguage()
    }

    private fun loadCurrentLanguage() {
        viewModelScope.launch {
            _currentLanguage.value = languageManager.getCurrentLanguage()
        }
    }

    fun setLanguage(languageCode: String) {
        viewModelScope.launch {
            languageManager.setLanguage(languageCode)
            _currentLanguage.value = languageCode
        }
    }

    fun getAvailableLanguages(): List<Locale> {
        return listOf(
            Locale("en"), // Английский
            Locale("ru")  // Русский
        )
    }
}
