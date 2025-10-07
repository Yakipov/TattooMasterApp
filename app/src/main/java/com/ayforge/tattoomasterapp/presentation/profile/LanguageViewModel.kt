package com.ayforge.tattoomasterapp.presentation.profile

import android.app.Activity
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

    private val _currentLanguage = MutableStateFlow(Locale.getDefault().language)
    val currentLanguage: StateFlow<String> = _currentLanguage

    init {
        // Загружаем сохранённый язык при инициализации
        viewModelScope.launch {
            val saved = languageManager.getCurrentLanguage()
            _currentLanguage.value = saved
        }
    }

    fun getAvailableLanguages(): List<Locale> {
        return listOf(Locale("en"), Locale("ru"))
    }

    fun setLanguage(language: String, activity: Activity?) {
        viewModelScope.launch {
            languageManager.setLanguage(language)
            _currentLanguage.value = language
            activity?.recreate() // перезапуск Activity для обновления ресурсов
        }
    }
}
