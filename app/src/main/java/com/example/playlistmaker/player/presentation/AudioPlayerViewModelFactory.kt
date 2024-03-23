package com.example.playlistmaker.player.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.Impl.AudioPlayerInteractorImpl

class AudioPlayerViewModelFactory(
    private val interactor: AudioPlayerInteractor,
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AudioPlayerViewModel(interactor) as T
    }
}