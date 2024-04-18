package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.presentation.SearchActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchViewModelModule = module {
    viewModel {
        SearchActivityViewModel(get())
    }
}