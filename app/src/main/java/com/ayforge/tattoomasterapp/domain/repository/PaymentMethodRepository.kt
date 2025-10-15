package com.ayforge.tattoomasterapp.domain.repository

interface PaymentMethodRepository {
    suspend fun getAll(): List<String>
    suspend fun addIfNew(name: String)
}
