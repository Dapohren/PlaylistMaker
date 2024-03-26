package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {
    fun loadIsDarkTheme(): ThemeSettings
    fun saveIsDarkTheme(settings: ThemeSettings)
}