package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interfaces.FavTracksInteractor
import com.example.playlistmaker.media.domain.interfaces.SharedPrefMediaInteractor
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteSongsViewModel(private val interactorFavTracks: FavTracksInteractor, private val sharedPreferencesInteractor: SharedPrefMediaInteractor) : ViewModel() {
    private val _state = MutableLiveData<FavouriteStates>()
    val state: LiveData<FavouriteStates> = _state

    fun getFavouriteTracks() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                interactorFavTracks
                    .getAllTracksFromFav()
                    .collect() {
                        if(it.isEmpty()) {
                            _state.postValue(FavouriteStates.Empty)
                        } else {
                            _state.postValue(FavouriteStates.NotEmpty(it))
                        }
                    }
            }
        }
    }


    fun readFromSharedPreferences(): ArrayList<DataSongs>{
        return sharedPreferencesInteractor.readFromSharedPreferences()
    }

    fun writeToSharedPreferences(trackList: ArrayList<DataSongs>){
        sharedPreferencesInteractor.writeToSharedPreferences(trackList)
    }

}