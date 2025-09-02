package com.ayforge.tattoomasterapp.data.repository

import com.ayforge.tattoomasterapp.domain.model.User
import com.ayforge.tattoomasterapp.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

class UserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : UserRepository {

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser

        return firebaseUser?.let {
            User(
                id = it.uid,
                name = it.displayName ?: "Без имени",
                email = it.email ?: "Нет почты"
            )
        }
    }
    override fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}
