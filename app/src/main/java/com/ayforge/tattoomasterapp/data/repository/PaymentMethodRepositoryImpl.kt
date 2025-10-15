package com.ayforge.tattoomasterapp.data.repository

import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.ayforge.tattoomasterapp.data.local.dao.PaymentMethodDao
import com.ayforge.tattoomasterapp.data.local.entity.PaymentMethodEntity
import com.ayforge.tattoomasterapp.domain.repository.PaymentMethodRepository

class PaymentMethodRepositoryImpl(
    private val dao: PaymentMethodDao,
    private val sessionManager: SessionManager
) : PaymentMethodRepository {

    private fun currentUserId(): String =
        sessionManager.userId ?: throw IllegalStateException("User not signed in")

    override suspend fun getAll(): List<String> {
        val userId = try { currentUserId() } catch (_: Exception) { "" }
        // если пользователь не задан — вернём только глобальные (userId IS NULL)
        return if (userId.isBlank()) {
            dao.getAll().map { it.name }
        } else {
            dao.getAllForUser(userId).map { it.name }
        }
    }

    override suspend fun addIfNew(name: String) {
        if (name.isBlank()) return
        val userId = try { currentUserId() } catch (_: Exception) { null }
        // сохраняем метод привязанным к текущему пользователю (если есть)
        dao.insert(PaymentMethodEntity(name = name, userId = userId))
    }
}
