package com.rogergcc.encryptedsharedpreferencessample.encrypt

import android.content.SharedPreferences
import androidx.core.content.edit


/**
 * Created on enero.
 * year 2024 .
 */
class SharedPreferencesMigrator(
    private val oldSharedPreferences: SharedPreferences,
    private val newSharedPreferences: SharedPreferences
) {
    companion object {
        private const val CRYPTO_KEYSET_KEY = "__androidx_security_crypto_encrypted_prefs_key_keyset__"
        private const val CRYPTO_VALUE_KEYSET_KEY = "__androidx_security_crypto_encrypted_prefs_value_keyset__"
    }

    fun migrateAllData() {
//        val allKeys = oldSharedPreferences.all.keys.toList()
//        migrateData(allKeys)

        val allKeys = oldSharedPreferences.all.keys.toList()

        // Obtener todos los valores temporalmente
        val allData = mutableMapOf<String, Any?>()
        allKeys.forEach { key ->
            allData[key] = getValueFromOldPreferences(key)
        }

        // Limpiar preferencias antiguas, excluyendo las claves relacionadas con el cifrado
        clearOldPreferencesExceptKeys(oldSharedPreferences, setOf(CRYPTO_KEYSET_KEY, CRYPTO_VALUE_KEYSET_KEY))

        // Migrar datos al mismo archivo de preferencias
        allData.forEach { (key, value) ->
            migrateKey(key, value)
        }

    }

//    private fun migrateData(keysToMigrate: List<String>) {
//        keysToMigrate.forEach { key ->
//            migrateKey(key)
//        }
////        oldSharedPreferences.edit().clear().apply()
//
//    }

    private fun migrateKey(key: String, value: Any?) {
        // Excluir las claves relacionadas con el cifrado
        if (!isCryptoKey(key)) {
            when (value) {
                is String -> newSharedPreferences.edit().putString(key, value).apply()
                is Int -> newSharedPreferences.edit().putInt(key, value).apply()
                is Boolean -> newSharedPreferences.edit().putBoolean(key, value).apply()
                is Long -> newSharedPreferences.edit().putLong(key, value).apply()
                is Float -> newSharedPreferences.edit().putFloat(key, value).apply()
                is Set<*> -> newSharedPreferences.edit().putStringSet(key, value as Set<String>).apply()
            }
        }
    }

    private fun isCryptoKey(key: String): Boolean {
        return key == CRYPTO_KEYSET_KEY || key == CRYPTO_VALUE_KEYSET_KEY
    }

    private fun clearOldPreferencesExceptKeys(
        sharedPreferences: SharedPreferences,
        keysToKeep: Set<String>
    ) {
        sharedPreferences.edit {
            sharedPreferences.all.keys.filter { it !in keysToKeep }.forEach { key ->
                remove(key)
            }
            apply()
        }
    }

    private fun getValueFromOldPreferences(key: String): Any? {
        return oldSharedPreferences.all[key]
    }


}