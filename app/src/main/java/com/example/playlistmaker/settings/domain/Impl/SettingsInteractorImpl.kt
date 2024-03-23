package com.example.playlistmaker.settings.domain.Impl

import com.example.playlistmaker.settings.data.Impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) : SettingsInteractor {
    override fun loadIsDarkTheme(): SettingsRepositoryImpl.ThemeSettings {
        return settingsRepository.loadIsDarkTheme()
    }

    override fun saveIsDarkTheme(themeSettings: SettingsRepositoryImpl.ThemeSettings) {
        settingsRepository.saveIsDarkTheme(themeSettings)
    }
}