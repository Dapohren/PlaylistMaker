package com.example.playlistmaker.settings.domain


import com.example.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsInteractor {
    fun loadIsDarkTheme(): ThemeSettings
    fun saveIsDarkTheme(setting: ThemeSettings)
}