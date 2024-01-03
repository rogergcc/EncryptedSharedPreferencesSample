package com.rogergcc.encryptedsharedpreferencessample.preferences

import android.content.Context
import android.content.SharedPreferences


/**
 * Created on enero.
 * year 2024 .
 */
class SharedPreferencesManager private constructor(private val context: Context, private val fileName: String) {

    private var sharedPreferences: SharedPreferences? = null

    init {
        // Do the initialization only once
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        }
    }

    fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences ?: throw IllegalStateException("SharedPreferences not initialized")
    }

    companion object {
        private var instances = mutableMapOf<String, SharedPreferencesManager>()

        fun getInstance(context: Context, fileName: String): SharedPreferencesManager {
            return instances[fileName] ?: synchronized(this) {
                instances[fileName] ?: SharedPreferencesManager(context.applicationContext, fileName).also {
                    instances[fileName] = it
                }
            }
        }
    }
}