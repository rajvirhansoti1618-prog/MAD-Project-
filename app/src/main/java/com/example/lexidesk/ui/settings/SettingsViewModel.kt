package com.example.lexidesk.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexidesk.LexiDeskApp
import com.example.lexidesk.data.local.UserProfile
import com.example.lexidesk.utils.BackupRestoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(private val app: LexiDeskApp) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = app.userPreferencesRepository.userProfileFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserProfile()
    )

    fun updateProfile(name: String, profession: String, firmName: String, email: String, theme: String) {
        viewModelScope.launch {
            app.userPreferencesRepository.saveProfile(name, profession, firmName, email, theme)
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            app.userPreferencesRepository.setThemeMode(theme)
        }
    }

    fun setAppLock(enabled: Boolean, pin: String) {
        viewModelScope.launch {
            app.userPreferencesRepository.setAppLock(enabled, pin)
        }
    }

    fun exportBackup(context: Context, onResult: (File?) -> Unit) {
        viewModelScope.launch {
            val file = BackupRestoreManager.exportBackupJson(context, app)
            onResult(file)
        }
    }

    fun restoreBackup(jsonString: String, onResult: (Result<Int>) -> Unit) {
        viewModelScope.launch {
            val res = BackupRestoreManager.restoreBackupJson(app, jsonString)
            onResult(res)
        }
    }

    fun resetOnboarding(onReset: () -> Unit) {
        viewModelScope.launch {
            app.userPreferencesRepository.resetOnboarding()
            onReset()
        }
    }
}
