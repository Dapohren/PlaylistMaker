package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.settings.data.Impl.SettingsRepositoryImpl

interface SettingsRepository {
    fun loadIsDarkTheme(): SettingsRepositoryImpl.ThemeSettings
    fun saveIsDarkTheme(settings: SettingsRepositoryImpl.ThemeSettings)
}