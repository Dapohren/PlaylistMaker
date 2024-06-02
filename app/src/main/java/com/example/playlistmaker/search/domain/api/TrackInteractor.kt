package com.example.playlistmaker.search.domain.api
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.flow.Flow


interface TrackInteractor {

    fun searchTracks(expression: String) : Flow<Pair<List<DataSongs>?, Int?>>


    fun readFromSharedPreferences(): ArrayList<DataSongs>

    fun writeToSharedPreferences(trackList: ArrayList<DataSongs>)
}