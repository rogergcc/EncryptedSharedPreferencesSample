package com.rogergcc.encryptedsharedpreferencessample.encrypt

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys


/**
 * Created on diciembre.
 * year 2023 .
 */
class EncryptedSharedPreferencesManager private constructor(
    private val context: Context,
    private val fileName: String,
) {

    private var sharedPreferences: SharedPreferences? = null

    init {
        if (sharedPreferences == null) {
            val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
            val masterKeyAlias: String
            try {
                masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
                sharedPreferences = EncryptedSharedPreferences.create(
                    fileName,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences ?: throw IllegalStateException("SharedPreferences not initialized")
    }

    companion object {
        private var instances = mutableMapOf<String, EncryptedSharedPreferencesManager>()

        fun getInstance(context: Context, fileName: String): EncryptedSharedPreferencesManager {
            return instances[fileName] ?: synchronized(this) {
                instances[fileName] ?: EncryptedSharedPreferencesManager(
                    context.applicationContext,
                    fileName
                ).also {
                    instances[fileName] = it
                }
            }
        }
    }
}