package com.ayforge.tattoomasterapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayforge.tattoomasterapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _username = MutableStateFlow("Загрузка...")
    val username: StateFlow<String> = _username

    init {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser()
            _username.value = user?.name ?: "Неизвестный пользователь"
        }
    }
}
