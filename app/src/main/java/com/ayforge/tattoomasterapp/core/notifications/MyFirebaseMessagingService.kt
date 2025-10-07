package com.ayforge.tattoomasterapp.core.notifications

import android.util.Log
import com.ayforge.tattoomasterapp.data.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Новый токен: $token")

        // UserRepositoryImpl требует context + firebaseAuth
        val userRepository = UserRepositoryImpl(
            context = applicationContext,
            firebaseAuth = FirebaseAuth.getInstance()
        )

        CoroutineScope(Dispatchers.IO).launch {
            userRepository.saveFcmToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "TattooMasterApp"
        val body = remoteMessage.notification?.body ?: "Уведомление"

        Log.d("FCM", "Пуш получен: $title - $body")

        // Покажем уведомление через твой NotificationHelper
        NotificationHelper.showNotification(
            context = applicationContext,
            channelId = NotificationHelper.CHANNEL_GENERAL, // или reminders — зависит от типа пуша
            title = title,
            message = body,
            notificationId = System.currentTimeMillis().toInt()
        )
    }
}
