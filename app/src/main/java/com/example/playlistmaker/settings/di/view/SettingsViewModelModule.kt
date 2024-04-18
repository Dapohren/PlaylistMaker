package com.example.playlistmaker.settings.di.view


import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsViewModelModule = module {
    viewModel {
        SettingsViewModel(get(), get())
    }
}