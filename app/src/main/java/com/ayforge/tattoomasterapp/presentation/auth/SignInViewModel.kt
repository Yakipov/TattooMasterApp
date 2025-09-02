package com.ayforge.tattoomasterapp.presentation.auth

import androidx.lifecycle.ViewModel
import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SignInUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class SignInViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    private val _isSignedIn = MutableStateFlow(false)
    val uiState: StateFlow<SignInUiState> = _uiState
    val isSignedIn: StateFlow<Boolean> = _isSignedIn

    fun signIn(email: String, password: String) {
        _uiState.value = SignInUiState(isLoading = true)

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _uiState.value = SignInUiState(isLoading = false)

                if (task.isSuccessful) {
                    sessionManager.isUserSignedIn = true // ✅ сохраняем сессию
                    _isSignedIn.value = true
                } else {
                    _uiState.value = SignInUiState(
                        error = task.exception?.message ?: "Ошибка входа"
                    )
                }
            }
    }
}
