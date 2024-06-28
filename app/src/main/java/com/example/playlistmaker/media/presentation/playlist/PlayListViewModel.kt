package com.example.playlistmaker.media.presentation.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interfaces.playlist.PlaylistInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayListViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private val _state = MutableLiveData<PlaylistState>()
    val state: LiveData<PlaylistState> = _state

    fun getPlaylists(){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                playlistInteractor
                    .getPlaylists()
                    .collect {
                        if(it.isEmpty())
                            _state.postValue(PlaylistState.Empty)
                        else
                            _state.postValue(PlaylistState.NotEmpty(it))
                    }
            }
        }
    }
}