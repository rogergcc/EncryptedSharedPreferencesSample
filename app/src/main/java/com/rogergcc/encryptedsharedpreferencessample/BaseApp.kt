package com.rogergcc.encryptedsharedpreferencessample

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.rogergcc.encryptedsharedpreferencessample.encrypt.EncryptedSharedPreferencesManager
import com.rogergcc.encryptedsharedpreferencessample.encrypt.SharedPreferencesMigrator
import com.rogergcc.encryptedsharedpreferencessample.preferences.SharedPreferencesManager


/**
 * Created on enero.
 * year 2024 .
 */
class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        SharedPreferencesManager.getInstance(this, FILENAME_PREFERENCES)
//        EncryptedSharedPreferencesManager.getInstance(this, FILENAME_PREFERENCES)
//        SharedPreferencesManager.getInstance(this, FILENAME_PREFERENCES_2)
//        migrateSharedPreferences(FILENAME_PREFERENCES, FILENAME_PREFERENCES)

    }

    companion object {
         const val TAG = "BaseApp"
        const val FILENAME_PREFERENCES: String = "app_prefs"

        const val FILENAME_PREFERENCES_2: String = "Conf"
        const val FILENAME_PREFERENCES_3: String = "Conf_dev"
    }

    private fun migrateSharedPreferences(oldFileName: String, newFileName: String) {
        if (sharedPreferencesExist(oldFileName)) {
            Log.d(TAG, "migrateSharedPreferences: Migrando SharedPreferences")
            val oldPrefs = SharedPreferencesManager.getInstance(this, oldFileName).getSharedPreferences()
            val encryptedPrefs = EncryptedSharedPreferencesManager.getInstance(this,newFileName).getSharedPreferences()


            val migrator = SharedPreferencesMigrator(oldPrefs, encryptedPrefs)
            migrator.migrateAllData()
        }
    }


    private fun sharedPreferencesExist(fileName: String): Boolean {
        return try {
            SharedPreferencesManager.getInstance(this, fileName).getSharedPreferences().all.isNotEmpty()
        } catch (e: Exception) {
            // Puedes manejar la excepción si hay un problema al obtener las SharedPreferences
            false
        }
    }
}