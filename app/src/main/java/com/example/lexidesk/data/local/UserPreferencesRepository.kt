package com.example.lexidesk.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lexidesk_preferences")

data class UserProfile(
    val name: String = "",
    val profession: String = "Lawyer", // Lawyer, Chartered Accountant, Consultant, Other
    val firmName: String = "",
    val email: String = "",
    val isOnboarded: Boolean = false,
    val themeMode: String = "System", // System, Light, Dark
    val isAppLockEnabled: Boolean = false,
    val appLockPin: String = ""
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val PROFESSION = stringPreferencesKey("profession")
        val FIRM_NAME = stringPreferencesKey("firm_name")
        val EMAIL = stringPreferencesKey("email")
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val APP_LOCK_PIN = stringPreferencesKey("app_lock_pin")
        val SAMPLE_DATA_LOADED = booleanPreferencesKey("sample_data_loaded")
    }

    val userProfileFlow: Flow<UserProfile> = context.dataStore.data.map { preferences ->
        UserProfile(
            name = preferences[PreferencesKeys.USER_NAME] ?: "",
            profession = preferences[PreferencesKeys.PROFESSION] ?: "Lawyer",
            firmName = preferences[PreferencesKeys.FIRM_NAME] ?: "",
            email = preferences[PreferencesKeys.EMAIL] ?: "",
            isOnboarded = preferences[PreferencesKeys.IS_ONBOARDED] ?: false,
            themeMode = preferences[PreferencesKeys.THEME_MODE] ?: "System",
            isAppLockEnabled = preferences[PreferencesKeys.APP_LOCK_ENABLED] ?: false,
            appLockPin = preferences[PreferencesKeys.APP_LOCK_PIN] ?: ""
        )
    }

    val isSampleDataLoadedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SAMPLE_DATA_LOADED] ?: false
    }

    suspend fun saveProfile(
        name: String,
        profession: String,
        firmName: String,
        email: String,
        themeMode: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
            preferences[PreferencesKeys.PROFESSION] = profession
            preferences[PreferencesKeys.FIRM_NAME] = firmName
            preferences[PreferencesKeys.EMAIL] = email
            preferences[PreferencesKeys.THEME_MODE] = themeMode
            preferences[PreferencesKeys.IS_ONBOARDED] = true
        }
    }

    suspend fun setThemeMode(themeMode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode
        }
    }

    suspend fun setAppLock(enabled: Boolean, pin: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_LOCK_ENABLED] = enabled
            // Store the SHA-256 hash of the PIN instead of the raw value.
            // NOTE: This is a one-way upgrade — any previously stored plain-text PINs
            // will be invalidated and the user must set a new PIN.
            preferences[PreferencesKeys.APP_LOCK_PIN] = if (pin.isBlank()) "" else hashPin(pin)
        }
    }

    /**
     * Returns the SHA-256 hex digest of the given PIN string.
     * Used to avoid storing the raw PIN in DataStore.
     */
    fun hashPin(pin: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun setSampleDataLoaded(loaded: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SAMPLE_DATA_LOADED] = loaded
        }
    }

    suspend fun resetOnboarding() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ONBOARDED] = false
        }
    }
}
