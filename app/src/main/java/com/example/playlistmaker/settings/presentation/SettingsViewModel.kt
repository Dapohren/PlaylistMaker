package com.example.playlistmaker.settings.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.Impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.Impl.SharingInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SharingInteractor

import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.util.Creator

class SettingsViewModel(private val settingsInteractor: SettingsInteractor, private val sharingInteractor: SharingInteractor) : ViewModel(){

   /* private val settingsInteractor by lazy {
        Creator.provideSettingsInteractor(context = getApplication<Application>())
    }

    private val sharingInteractor by lazy {
        Creator.provideSharingInteractor(context = getApplication<Application>())
    }*/

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