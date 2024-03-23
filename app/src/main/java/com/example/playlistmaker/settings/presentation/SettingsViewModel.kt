package com.example.playlistmaker.settings.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.settings.data.Impl.SettingsRepositoryImpl
import com.example.playlistmaker.util.Creator

class SettingsViewModel(application: Application
) : AndroidViewModel(application) {

    /*companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }*/

    private val settingsInteractor by lazy {
        Creator.provideSettingsInteractor(context = getApplication<Application>())
    }

    private val sharingInteractor by lazy {
        Creator.provideSharingInteractor(context = getApplication<Application>())
    }

    fun updateThemeSettings(checked: Boolean) {
        if (checked) {
            settingsInteractor.saveIsDarkTheme(SettingsRepositoryImpl.ThemeSettings.DARK)
        } else {
            settingsInteractor.saveIsDarkTheme(SettingsRepositoryImpl.ThemeSettings.LIGHT)
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