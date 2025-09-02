package com.ayforge.tattoomasterapp.domain.usecase

import com.ayforge.tattoomasterapp.domain.repository.UserRepository

class GetCurrentUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke() = repository.getCurrentUser()
}
