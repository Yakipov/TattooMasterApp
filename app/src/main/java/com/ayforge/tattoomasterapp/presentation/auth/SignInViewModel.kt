package com.ayforge.tattoomasterapp.presentation.auth

import androidx.lifecycle.ViewModel
import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class SignInViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    fun signIn() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Введите email и пароль")
            return
        }

        _uiState.value = state.copy(isLoading = true, error = null)

        firebaseAuth.signInWithEmailAndPassword(state.email, state.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sessionManager.isUserSignedIn = true
                    _uiState.value = state.copy(isLoading = false, success = true)
                } else {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = task.exception?.message ?: "Ошибка входа"
                    )
                }
            }
    }

    fun resetPassword() {
        val state = _uiState.value
        if (state.email.isBlank()) {
            _uiState.value = state.copy(error = "Введите email для сброса пароля")
            return
        }

        firebaseAuth.sendPasswordResetEmail(state.email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = state.copy(error = "Письмо для сброса пароля отправлено")
                } else {
                    _uiState.value = state.copy(
                        error = task.exception?.message ?: "Ошибка сброса пароля"
                    )
                }
            }
    }
}
