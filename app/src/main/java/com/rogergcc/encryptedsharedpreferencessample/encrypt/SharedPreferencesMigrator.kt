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
    }

    private fun migrateKey(key: String) {
        when (val value = getValueFromOldPreferences(key)) {
            is String -> newSharedPreferences.edit().putString(key, value).apply()
            is Int -> newSharedPreferences.edit().putInt(key, value).apply()
            is Long -> newSharedPreferences.edit().putLong(key, value).apply()
        }
    }

    private fun getValueFromOldPreferences(key: String): Any? {
        return oldSharedPreferences.all[key]
    }
}