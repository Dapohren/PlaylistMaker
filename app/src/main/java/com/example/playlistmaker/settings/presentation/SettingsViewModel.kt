package com.example.playlistmaker.settings.presentation


import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SharingInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings

class SettingsViewModel(private val settingsInteractor: SettingsInteractor, private val sharingInteractor: SharingInteractor) : ViewModel(){



    fun updateThemeSettings(checked: Boolean) {
        if (checked) {
            settingsInteractor.saveIsDarkTheme(ThemeSettings.DARK)
        } else {
            settingsInteractor.saveIsDarkTheme(ThemeSettings.LIGHT)
        }
    }

    fun shareApp(url: String, title: String) {
        sharingInteractor.shareApp(link = url, title = title)
    }

    fun openSupport(email: String, subject: String, text: String) {
        sharingInteractor.openSupport(email = email, subject = subject, text = text)
    }

    fun openTerms(url: String) {
        sharingInteractor.openTerms(link = url)
    }


}