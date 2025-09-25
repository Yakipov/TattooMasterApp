package com.ayforge.tattoomasterapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class SignUpViewModel(
    private val auth: FirebaseAuth,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    fun onConfirmPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = newPassword)
    }

    fun signUp() {
        val state = _uiState.value
        if (state.password != state.confirmPassword) {
            _uiState.value = state.copy(error = "Пароли не совпадают")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            auth.createUserWithEmailAndPassword(state.email, state.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            sessionManager.isUserSignedIn = true
                            sessionManager.userId = user.uid  // сохраняем UID
                        }
                        _uiState.value = state.copy(isLoading = false, success = true)
                    } else {
                        _uiState.value = state.copy(
                            isLoading = false,
                            error = task.exception?.message ?: "Ошибка регистрации"
                        )
                    }
                }
        }
    }
}
