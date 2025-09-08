package com.ayforge.tattoomasterapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.ayforge.tattoomasterapp.presentation.navigation.DrawerScreen
import com.ayforge.tattoomasterapp.ui.theme.TattooMasterAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TattooMasterAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    DrawerScreen(
                        onLogout = {
                            // 🔑 пока просто заглушка — уходим на экран SignIn
                            // (навигация внутри DrawerScreen → AppNavGraph)
                        }
                    )
                }
            }
        }
    }
}
