package com.example.playlistmaker.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.playlistmaker.app.App
import com.example.playlistmaker.R

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
        switchDarkMode.isChecked = (application as App).darkTheme
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
            (application as App).saveState()
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