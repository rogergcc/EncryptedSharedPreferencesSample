package com.rogergcc.encryptedsharedpreferencessample.encrypt

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


/**
 * Created on diciembre.
 * year 2023 .
 */
class EncryptedPrefsImpl(context: Context) {
    private companion object {
        const val PREFERENCES_NAME = "shared_encrypted_prefs_impl"
    }

    private val spec = KeyGenParameterSpec.Builder(
        MasterKey.DEFAULT_MASTER_KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    ).apply {
        setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
    }.build()

    // Step 1: Create or retrieve the Master Key for encryption/decryption
    private val masterKey = MasterKey.Builder(context).apply {
        setKeyGenParameterSpec(spec)
    }.build()

    // Step 2: Initialize/open an instance of EncryptedSharedPreferences
    val preferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFERENCES_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}