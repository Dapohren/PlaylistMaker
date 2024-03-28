package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.presentation.AudioPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val audioPlayerViewModelModule = module {
    viewModel {
        AudioPlayerViewModel(get())
    }
}