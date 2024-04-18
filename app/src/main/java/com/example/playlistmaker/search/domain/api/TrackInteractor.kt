package com.example.playlistmaker.search.domain.api
import com.example.playlistmaker.search.domain.models.DataSongs


interface TrackInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)
    fun interface TracksConsumer {
        fun consume(foundTracks: List<DataSongs>?, errorMessage: Int?)
    }

    fun readFromSharedPreferences(): ArrayList<DataSongs>

    fun writeToSharedPreferences(trackList: ArrayList<DataSongs>)
}