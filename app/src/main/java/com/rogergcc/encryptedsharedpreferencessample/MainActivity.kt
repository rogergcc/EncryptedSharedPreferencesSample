package com.rogergcc.encryptedsharedpreferencessample

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.rogergcc.encryptedsharedpreferencessample.databinding.ActivityMainBinding
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

        binding.initEncrypted.setOnCheckedChangeListener { _, checked -> initSharedPreferences(checked) }
        binding.saveButton.setOnClickListener { saveValue() }
        binding.readButton.setOnClickListener { readValue() }

        binding.initEncrypted.isChecked = true
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
        getSharedPreferences(FILENAME, MODE_PRIVATE)
            .edit()
            .clear()
            .commit() //note: I use `commit` in order to measure raw performance. Please use `apply` in your apps
    }

    private fun initEncryptedSharedPreferences() {
        val startTs = System.currentTimeMillis()


//         val spec = KeyGenParameterSpec.Builder(
//            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
//            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
//        ).apply {
//            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
//            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
//            setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
//        }.build()
//
//        // Step 1: Create or retrieve the Master Key for encryption/decryption
//         val masterKey = MasterKey.Builder(applicationContext).apply {
//            setKeyGenParameterSpec(spec)
//        }.build()
//
//        sharedPreferences =  EncryptedSharedPreferences.create(
//            applicationContext,
//            preferencesName,
//            masterKey,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
//        sharedPreferences = getSecretSharedPref(applicationContext)
        val masterKey = MasterKey.Builder(applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        sharedPreferences = EncryptedSharedPreferences.create(
            applicationContext,
            FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val endTs = System.currentTimeMillis()
        binding.initTimestamp.visibility = View.VISIBLE
        binding.initTimestamp.text = getString(R.string.timestamp).format(endTs - startTs)
    }


    private fun getSecretSharedPref(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(context,
            FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun initCleartextSharedPreferences() {
        val startTs = System.currentTimeMillis()

        sharedPreferences = getSharedPreferences(FILENAME, MODE_PRIVATE)

        val endTs = System.currentTimeMillis()
        binding.initTimestamp.visibility = View.VISIBLE
        binding.initTimestamp.text = getString(R.string.timestamp).format(endTs - startTs)
    }

    private fun saveValue() {
        val startTs = System.currentTimeMillis()

        // Step 3: Save data to the EncryptedSharedPreferences as usual
        sharedPreferences.edit()
            .putString(KEYVALUE, binding.saveText.text.toString())
            .commit() //note: I use `commit` in order to measure raw performance. Please use `apply` in your apps

        val endTs = System.currentTimeMillis()
        binding.saveTimestamp.visibility = View.VISIBLE
        binding.saveTimestamp.text = getString(R.string.timestamp).format(endTs - startTs)

        hideKeyboard()
        showRawFile()
    }
    private fun readValue() {
        val startTs = System.currentTimeMillis()

        // Step 3: Read data from EncryptedSharedPreferences as usual
        val value = sharedPreferences.getString(KEYVALUE, "")
        binding.readText.setText(value)

        val endTs = System.currentTimeMillis()
        binding.readTimestamp.visibility = View.VISIBLE
        binding.readTimestamp.text = getString(R.string.timestamp).format(endTs - startTs)

        hideKeyboard()
        showRawFile()
    }

    private fun showRawFile() {
        val preferencesFile = File("${applicationInfo.dataDir}/shared_prefs/${Companion.FILENAME}.xml")
        if (preferencesFile.exists()) {
            binding.fileText.text = preferencesFile.readText().highlight()
        } else {
            binding.fileText.text = ""
        }
    }

    companion object {
        private const val FILENAME = "shared_preferences_encrypted"
        private const val KEYVALUE: String = "KEY_VALUE"
    }

}