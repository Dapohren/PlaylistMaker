package com.example.playlistmaker.settings.di.domain


import com.example.playlistmaker.settings.domain.Impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.Impl.SharingInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SharingInteractor

import org.koin.dsl.module

val settingsDomainModule = module {
    factory <SharingInteractor> {
        SharingInteractorImpl(get())
    }

    factory <SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
}