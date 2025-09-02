package com.ayforge.tattoomasterapp.domain.usecase

import com.ayforge.tattoomasterapp.data.local.entity.ClientEntity
import com.ayforge.tattoomasterapp.domain.repository.ClientRepository

class CheckClientExistsUseCase(
    private val clientRepository: ClientRepository
) {
    suspend operator fun invoke(name: String, phone: String): ClientEntity? {
        return clientRepository.getByNameAndPhone(name, phone)
    }
}
