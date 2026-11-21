package com.allinone.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

/**
 * Non-sensitive application settings stored in DataStore. Secrets (API keys,
 * credentials) live in EncryptedPrefs instead.
 */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = intPreferencesKey("theme_mode") // 0 system, 1 light, 2 dark
        val SPACE_2D = booleanPreferencesKey("space_2d_mode")
        val AI_PROVIDER = stringPreferencesKey("ai_provider")
        val AI_MODEL = stringPreferencesKey("ai_model")
        val BACKUP_AUTO = booleanPreferencesKey("backup_auto")
        val OCR_LANGUAGE = stringPreferencesKey("ocr_language")
    }

    val themeMode: Flow<Int> = context.dataStore.data.map { it[Keys.THEME_MODE] ?: 0 }
    val space2dMode: Flow<Boolean> = context.dataStore.data.map { it[Keys.SPACE_2D] ?: false }
    val aiProvider: Flow<String> = context.dataStore.data.map { it[Keys.AI_PROVIDER] ?: "openai" }
    val aiModel: Flow<String> = context.dataStore.data.map { it[Keys.AI_MODEL] ?: "" }
    val backupAuto: Flow<Boolean> = context.dataStore.data.map { it[Keys.BACKUP_AUTO] ?: true }
    val ocrLanguage: Flow<String> = context.dataStore.data.map { it[Keys.OCR_LANGUAGE] ?: "ro" }

    suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode }
    }

    suspend fun setSpace2dMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SPACE_2D] = enabled }
    }

    suspend fun setAiProvider(provider: String) {
        context.dataStore.edit { it[Keys.AI_PROVIDER] = provider }
    }

    suspend fun setAiModel(model: String) {
        context.dataStore.edit { it[Keys.AI_MODEL] = model }
    }

    suspend fun setBackupAuto(enabled: Boolean) {
        context.dataStore.edit { it[Keys.BACKUP_AUTO] = enabled }
    }

    suspend fun setOcrLanguage(language: String) {
        context.dataStore.edit { it[Keys.OCR_LANGUAGE] = language }
    }
}
