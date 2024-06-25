package com.example.playlistmaker.playlist.di

import com.example.playlistmaker.playlist.presentation.PlaylistInformationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelInfoModule = module {
    viewModel {
        PlaylistInformationViewModel(get(), get(), get())
    }
}