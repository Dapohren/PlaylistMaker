package com.example.playlistmaker.media.presentation.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interfaces.playlist.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.PlaylistModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    fun createPlaylist(playlist: PlaylistModel){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                playlistInteractor.createPlaylist(playlist)
            }
        }
    }
}