package com.ayforge.tattoomasterapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.ayforge.tattoomasterapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
