package com.rogergcc.encryptedsharedpreferencessample

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.rogergcc.encryptedsharedpreferencessample.BaseApp.Companion.FILENAME_PREFERENCES
import com.rogergcc.encryptedsharedpreferencessample.databinding.ActivityMainBinding
import com.rogergcc.encryptedsharedpreferencessample.encrypt.EncryptedSharedPreferencesManager
import com.rogergcc.encryptedsharedpreferencessample.preferences.SharedPreferencesManager
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Step 0: EncryptedSharedPreferences take long to initialize/open, therefor it's better to do it only once and keep an instance
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)
        readValue()

//        binding.initEncrypted.setOnCheckedChangeListener { _, checked -> initSharedPreferences(checked) }
        binding.saveButton.setOnClickListener { saveValue() }
        binding.readButton.setOnClickListener { readValue() }

        binding.initEncrypted.isChecked = false
    }

    private fun initSharedPreferences(checked: Boolean) {
        resetSharedPreferences()

        if (checked) {
            initEncryptedSharedPreferences()
        } else {
            initCleartextSharedPreferences()
        }

        hideKeyboard()
        showRawFile()
    }
    private fun resetSharedPreferences() {
        getSharedPreferences(FILENAME_PREFERENCES, MODE_PRIVATE)
            .edit()
            .clear()
            .commit() //note: I use `commit` in order to measure raw performance. Please use `apply` in your apps
    }

    private fun initEncryptedSharedPreferences() {
        val startTs = System.currentTimeMillis()


        val masterKey = MasterKey.Builder(applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        sharedPreferences = EncryptedSharedPreferences.create(
            applicationContext,
            FILENAME_PREFERENCES,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val endTs = System.currentTimeMillis()
        binding.initTimestamp.visibility = View.VISIBLE
        binding.initTimestamp.text = getString(R.string.timestamp).format(endTs - startTs)

//        PreferenceManager.getInstance(this )?.migrateToEncryptedSharedPreferences(this)


    }


    private fun initCleartextSharedPreferences() {
        val startTs = System.currentTimeMillis()

        sharedPreferences = getSharedPreferencesBack()

        val endTs = System.currentTimeMillis()
        binding.initTimestamp.visibility = View.VISIBLE
        binding.initTimestamp.text = getString(R.string.timestamp).format(endTs - startTs)
    }

    private fun getSharedPreferencesBack() =
        SharedPreferencesManager.getInstance(this, FILENAME_PREFERENCES).getSharedPreferences()

    private fun saveValue() {
        val startTs = System.currentTimeMillis()

        // Step 3: Save data to the EncryptedSharedPreferences as usual
        getSharedPreferencesBack().edit()
            .putString(keyToken, binding.saveText.text.toString())
            .commit() //note: I use `commit` in order to measure raw performance. Please use `apply` in your apps

        getSharedPreferencesBack().edit()
            .putInt(keyNumber, binding.tvNumberPreferences.text.toString().toInt() )
            .commit()
        getSharedPreferencesBack().edit()
            .putBoolean(keyOnbard, binding.saveCheckBox.isChecked)
            .commit()

        val endTs = System.currentTimeMillis()
        binding.saveTimestamp.visibility = View.VISIBLE
        binding.saveTimestamp.text = getString(R.string.timestamp).format(endTs - startTs)

        hideKeyboard()
        showRawFile()
    }
    private fun readValue() {
        var readSharedPrefOrEncriptedSharedPref: SharedPreferences? = null
        var isDataSharedPref: String = ""
        if (binding.initEncrypted.isChecked.not()) {
            isDataSharedPref = "SharedPref"
            readSharedPrefOrEncriptedSharedPref= getSharedPreferencesBack()
        } else {
            isDataSharedPref = "EncryptedSharedPref"
            readSharedPrefOrEncriptedSharedPref= EncryptedSharedPreferencesManager.
            getInstance(this, FILENAME_PREFERENCES).getSharedPreferences()
        }

        val startTs = System.currentTimeMillis()

        // Step 3: Read data from EncryptedSharedPreferences as usual
        val value = readSharedPrefOrEncriptedSharedPref.getString(keyToken, "")
        val valueInt = readSharedPrefOrEncriptedSharedPref.getInt(keyNumber, 0)
        val valueBoolean = readSharedPrefOrEncriptedSharedPref.getBoolean(keyOnbard, false)

        binding.readText.setText("$isDataSharedPref ? - $valueInt - $valueBoolean")


        val endTs = System.currentTimeMillis()
        binding.readTimestamp.visibility = View.VISIBLE
        binding.readTimestamp.text = getString(R.string.timestamp).format(endTs - startTs)

        hideKeyboard()
        showRawFile()
    }

    private fun showRawFile() {
        val preferencesFile: File
        binding.fileHeader.text= "File ?"
        if (binding.initEncrypted.isChecked.not()) {
            binding.fileHeader.text= "SharedPref File"
            preferencesFile = File("${applicationInfo.dataDir}/shared_prefs/${FILENAME_PREFERENCES}.xml")
        } else {
            binding.fileHeader.text= "EncryptedSharedPref File"
            preferencesFile = File("${applicationInfo.dataDir}/shared_prefs/${FILENAME_PREFERENCES}.xml")
        }

        if (preferencesFile.exists()) {
            binding.fileText.text = preferencesFile.readText().highlight()
        } else {
            binding.fileText.text = ""
        }
    }

    companion object {
//        private const val FILENAME_PREFERENCES = "shared_preferences_app"
        private const val FILENAME_PREFERENCES2 = "Conf"
        private const val FILENAME_PREFERENCES3 = "Conf_dev"
        private const val keyToken: String = "FCM_TOKEN"
        private const val keyNumber: String = "KEY_VALUE_BOOLEAN"
        private const val keyOnbard: String = "KEY_VALUE_INT"
        private const val KEYVALUE_LONG: String = "KEY_VALUE_LONG"
        private const val KEYVALUE_FLOAT: String = "KEY_VALUE_FLOAT"


    }

}