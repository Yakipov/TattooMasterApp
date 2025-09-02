package com.ayforge.tattoomasterapp.presentation.profile

import androidx.lifecycle.ViewModel
import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _userState = MutableStateFlow<FirebaseUser?>(null)
    val userState: StateFlow<FirebaseUser?> = _userState.asStateFlow()

    init {
        _userState.value = firebaseAuth.currentUser
    }

    fun logout() {
        firebaseAuth.signOut()
        sessionManager.clearSession()
    }
}