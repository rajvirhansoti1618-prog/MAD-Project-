package com.example.lexidesk

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.lexidesk.ui.navigation.LexiDeskAppShell
import com.example.lexidesk.ui.navigation.Screen
import com.example.lexidesk.ui.settings.AppLockScreen
import com.example.lexidesk.ui.theme.LexiDeskTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as LexiDeskApp

        setContent {
            val userProfile by app.userPreferencesRepository.userProfileFlow.collectAsState(
                initial = com.example.lexidesk.data.local.UserProfile()
            )

            val darkTheme = when (userProfile.themeMode) {
                "Dark" -> true
                "Light" -> false
                else -> isSystemInDarkTheme()
            }

            // Notification permission request for Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { /* Handle or proceed */ }

                LaunchedEffect(Unit) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            LexiDeskTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isUnlocked by remember { mutableStateOf(!userProfile.isAppLockEnabled) }

                    if (userProfile.isAppLockEnabled && !isUnlocked && userProfile.isOnboarded) {
                        AppLockScreen(
                            correctPin = userProfile.appLockPin,
                            onUnlocked = { isUnlocked = true }
                        )
                    } else {
                        val startDestination = if (userProfile.isOnboarded) {
                            Screen.Dashboard.route
                        } else {
                            Screen.Onboarding.route
                        }

                        LexiDeskAppShell(
                            app = app,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}
