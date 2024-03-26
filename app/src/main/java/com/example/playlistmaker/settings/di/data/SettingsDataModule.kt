package com.example.playlistmaker.settings.di.data

import com.example.playlistmaker.settings.data.Impl.InternetAccessImpl
import com.example.playlistmaker.settings.data.Impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.InternetAccess
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val settingsDataModule = module {
    factory <InternetAccess>{
        InternetAccessImpl(androidContext())
    }
    factory <SettingsRepository>{
        SettingsRepositoryImpl(androidContext())
    }
}