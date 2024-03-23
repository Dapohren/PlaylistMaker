package com.example.playlistmaker.settings.ui

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.presentation.SettingsViewModel


class SettingsActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var switchDarkMode: SwitchCompat
    private lateinit var shareApp: Button
    private lateinit var supportButton: Button
    private lateinit var agreeButton: Button
    private lateinit var backButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        shareApp = binding.share
        supportButton = binding.help
        agreeButton = binding.agree
        backButton = binding.backButton

        backButton.setOnClickListener {
            this.finish()
        }

        supportButton.setOnClickListener {
            viewModel.openSupport(
                email = getString(R.string.recipientEt),
                subject = getString(R.string.supportTitle),
                text = getString(R.string.supportText)
            )
        }

        shareApp.setOnClickListener {
            viewModel.shareApp(
                url = getString(R.string.appID),
                title = getString(R.string.share_app_title)

            )
        }

        agreeButton.setOnClickListener {
            viewModel.openTerms(
                url = getString(R.string.agree)
            )
        }

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            binding.switchOff.isChecked = true
        }

        binding.switchOff.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateThemeSettings(isChecked)
        }



    }

}