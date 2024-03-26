package com.example.playlistmaker.search.presentation

import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.DataSongs

class SearchActivityViewModel(private val trackInteractor: TrackInteractor): ViewModel() {
    private val _state = MutableLiveData<SearchStates>()
    val state: LiveData<SearchStates> = _state

    fun onClearHistoryClicked(){
        _state.postValue(SearchStates.ClearHistory)
    }

    fun searchTextClearClicked() {
        _state.postValue(SearchStates.ClearTracks)
    }

    fun showHistoryTracksEditTextOnFocus(
        editText: EditText,
    ) {
        if(editText.text.isEmpty() and editText.hasFocus()){
            _state.postValue(SearchStates.History(true, readFromSharedPreferences()))
        } else {
            _state.postValue(SearchStates.History(false, readFromSharedPreferences()))
        }
    }

    fun searchTracks(text: String){
        if (text.isNotEmpty()) {
            _state.postValue(SearchStates.Loading)
            trackInteractor.searchTracks(text
            ) { foundTracks, errorMessage ->
                when {
                    errorMessage != null -> {
                        _state.postValue(SearchStates.Error)
                    }

                    foundTracks.isNullOrEmpty() -> {
                        _state.postValue(SearchStates.Empty)
                    }

                    else -> {
                        _state.postValue(SearchStates.Tracks(tracks = foundTracks))
                    }
                }
            }
        }
    }

    private fun readFromSharedPreferences(): ArrayList<DataSongs>{
        return trackInteractor.readFromSharedPreferences()
    }

    fun writeToSharedPreferences(trackList: ArrayList<DataSongs>){
        trackInteractor.writeToSharedPreferences(trackList)
    }
}