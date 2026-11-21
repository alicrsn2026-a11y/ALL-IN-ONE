package com.allinone.app.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Wrapper around EncryptedSharedPreferences backed by the Android Keystore.
 * Used for secrets: API keys, credentials, tokens. Plain settings that are not
 * sensitive live in a normal DataStore.
 */
class EncryptedPrefs private constructor(private val prefs: SharedPreferences) {

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, default: String = ""): String =
        prefs.getString(key, default) ?: default

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        prefs.getBoolean(key, default)

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun contains(key: String): Boolean = prefs.contains(key)

    companion object {
        const val KEY_API_KEY = "api_key"
        const val KEY_API_PROVIDER = "api_provider"
        const val KEY_API_BASE_URL = "api_base_url"
        const val KEY_EMAIL_SMTP_HOST = "email_smtp_host"
        const val KEY_EMAIL_SMTP_PORT = "email_smtp_port"
        const val KEY_EMAIL_USER = "email_user"
        const val KEY_EMAIL_PASSWORD = "email_password"
        const val KEY_DEVICE_LOCK = "device_lock_enabled"
        const val KEY_APP_LOCK = "app_lock_enabled"

        private const val PREFS_NAME = "secure_prefs"

        fun create(context: Context): EncryptedPrefs {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            val prefs = EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            return EncryptedPrefs(prefs)
        }
    }
}
