package com.example.playlistmaker.search.presentation

import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.launch

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
            viewModelScope.launch {
                trackInteractor
                    .searchTracks(text)
                    .collect {pair ->
                        processResult(pair.first, pair.second)

                    }
            }
        }
    }
    private fun processResult(foundTracks: List<DataSongs>?, errorMessage: Int?) {
        val tracks = mutableListOf<DataSongs>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> _state.postValue(SearchStates.Error)
            tracks.isEmpty() -> _state.postValue(SearchStates.Empty)
            else -> _state.postValue(SearchStates.Tracks(tracks))
        }
    }

    private fun readFromSharedPreferences(): ArrayList<DataSongs>{
        return trackInteractor.readFromSharedPreferences()
    }

    fun writeToSharedPreferences(trackList: ArrayList<DataSongs>){
        trackInteractor.writeToSharedPreferences(trackList)
    }
}