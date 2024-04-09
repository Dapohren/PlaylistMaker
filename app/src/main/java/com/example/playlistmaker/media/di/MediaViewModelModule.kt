package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.presentation.FavoritesSongsViewModel
import com.example.playlistmaker.media.presentation.PlayListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaViewModelModule = module {
    viewModel {
        PlayListViewModel()
    }
    viewModel {
        FavoritesSongsViewModel()
    }
}
