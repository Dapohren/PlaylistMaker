package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.settings.data.Impl.SettingsRepositoryImpl

interface SettingsInteractor {
    fun loadIsDarkTheme(): SettingsRepositoryImpl.ThemeSettings
    fun saveIsDarkTheme(setting: SettingsRepositoryImpl.ThemeSettings)
}