package com.rogergcc.encryptedsharedpreferencessample.encrypt

import android.content.SharedPreferences


/**
 * Created on enero.
 * year 2024 .
 */
class SharedPreferencesMigrator(
    private val oldSharedPreferences: SharedPreferences,
    private val newSharedPreferences: SharedPreferences
) {
    fun migrateAllData() {
        val allKeys = oldSharedPreferences.all.keys.toList()
        migrateData(allKeys)
    }

    private fun migrateData(keysToMigrate: List<String>) {
        keysToMigrate.forEach { key ->
            migrateKey(key)
        }
        oldSharedPreferences.edit().clear().apply()

    }

    private fun migrateKey(key: String) {
        if (key != "__androidx_security_crypto_encrypted_prefs_key_keyset__" &&
            key != "__androidx_security_crypto_encrypted_prefs_value_keyset__"
        ) {
            when (val value = getValueFromOldPreferences(key)) {

                is String -> newSharedPreferences.edit().putString(key, value).apply()
                is Int -> newSharedPreferences.edit().putInt(key, value).apply()
                is Boolean -> newSharedPreferences.edit().putBoolean(key, value).apply()
                is Long -> newSharedPreferences.edit().putLong(key, value).apply()
                is Float -> newSharedPreferences.edit().putFloat(key, value).apply()
                is Set<*> -> newSharedPreferences.edit().putStringSet(key, value as Set<String>)
                    .apply()
            }
        }
    }

    private fun getValueFromOldPreferences(key: String): Any? {
        return oldSharedPreferences.all[key]
    }
}