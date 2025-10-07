package com.ayforge.tattoomasterapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import com.ayforge.tattoomasterapp.domain.model.User
import com.ayforge.tattoomasterapp.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

class UserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val context: Context
) : UserRepository {

    companion object {
        private val KEY_FCM_TOKEN = stringPreferencesKey("fcm_token")
    }

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

    override suspend fun saveFcmToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FCM_TOKEN] = token
        }
    }

    override suspend fun getFcmToken(): String? {
        return context.dataStore.data
            .map { prefs -> prefs[KEY_FCM_TOKEN] }
            .first()
    }
}
