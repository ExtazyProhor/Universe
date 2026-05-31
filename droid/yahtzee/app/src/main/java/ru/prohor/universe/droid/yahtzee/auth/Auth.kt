package ru.prohor.universe.droid.yahtzee.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

object Auth {
    private const val KEY = "user_key"

    private lateinit var prefs: SharedPreferences

    fun initialize(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            "auth",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun hasKey(): Boolean {
        return !prefs.getString(KEY, null).isNullOrBlank()
    }

    fun key(): String? {
        return prefs.getString(KEY, "")
    }

    fun saveKey(key: String) {
        prefs.edit { putString(KEY, key) }
    }

    fun removeKey() {
        prefs.edit { remove(KEY) }
    }
}
