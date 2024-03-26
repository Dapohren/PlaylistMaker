package com.example.playlistmaker.search.presentation

import com.example.playlistmaker.search.domain.models.DataSongs

sealed class SearchStates {
    data object ClearHistory: SearchStates()
    class History(val isShown: Boolean, val history: ArrayList<DataSongs>): SearchStates()
    class Tracks(val tracks: List<DataSongs>): SearchStates()
    data object ClearTracks: SearchStates()
    data object Error: SearchStates()
    data object Empty: SearchStates()
    data object Loading: SearchStates()

}