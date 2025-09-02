package com.ayforge.tattoomasterapp.domain.repository

import com.ayforge.tattoomasterapp.domain.model.User

interface UserRepository {
    suspend fun getCurrentUser(): User?
    fun logout()
}

