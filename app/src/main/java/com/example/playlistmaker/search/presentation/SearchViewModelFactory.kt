package com.example.playlistmaker.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.search.domain.api.TrackInteractor

class SearchViewModelFactory(private val trackInteractor: TrackInteractor) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchActivityViewModel(trackInteractor) as T
    }
}