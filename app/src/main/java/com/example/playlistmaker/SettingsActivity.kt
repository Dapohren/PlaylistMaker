package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity: AppCompatActivity() {


    @SuppressLint("StringFormatInvalid", "SuspiciousIndentation", "MissingInflatedId")
    private lateinit var switchDarkMode: SwitchCompat
    @SuppressLint("StringFormatInvalid", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
        switchDarkMode = findViewById(R.id.switch_off)
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        recreateActivity()
                    }
                }
            } else {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_NO ->  {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        recreateActivity()
                    }
                }

            }
        }

        val shareApp: Button = findViewById(R.id.share)
        shareApp.setOnClickListener {
            val appId = "com.Practicum.PlaylistMaker"
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing, appId))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app_title)))
        }

        val supportButton: Button = findViewById(R.id.help)
        supportButton.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(getString(R.string.recipientEt))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.supportTitle))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.supportText))
                startActivity(this)
            }
        }
        val agreeButton: Button = findViewById(R.id.agree)
        agreeButton.setOnClickListener {
                val textAgree = getString(R.string.agree)
                val openIntent = Intent(Intent.ACTION_VIEW)
                openIntent.data = Uri.parse(textAgree)
                startActivity(openIntent)
        }
    }
    private fun recreateActivity() {
        recreate()
    }
}