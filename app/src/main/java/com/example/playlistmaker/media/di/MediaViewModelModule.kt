package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.presentation.FavouriteSongsViewModel
import com.example.playlistmaker.media.presentation.playlist.NewPlaylistViewModel
import com.example.playlistmaker.media.presentation.playlist.PlayListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaViewModelModule = module {
    viewModel {
        PlayListViewModel(get())
    }
    viewModel {
        FavouriteSongsViewModel(get(), get())
    }
    viewModel{
        NewPlaylistViewModel(get())
    }
}
