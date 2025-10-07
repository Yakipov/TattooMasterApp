package com.ayforge.tattoomasterapp.presentation.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayforge.tattoomasterapp.domain.model.User
import com.ayforge.tattoomasterapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _userState.value = userRepository.getCurrentUser()
        }
    }

    fun logout() {
        userRepository.logout()
    }

    // --- FCM методы ---
    fun saveFcmToken(token: String) {
        viewModelScope.launch {
            userRepository.saveFcmToken(token)
        }
    }

    fun getFcmToken(onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val token: String? = userRepository.getFcmToken()
            onResult(token)
        }
    }
}
