package com.example.playlistmaker.settings.domain.Impl


import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) : SettingsInteractor {
    override fun loadIsDarkTheme(): ThemeSettings {
        return settingsRepository.loadIsDarkTheme()
    }

    override fun saveIsDarkTheme(themeSettings: ThemeSettings) {
        settingsRepository.saveIsDarkTheme(themeSettings)
    }
}