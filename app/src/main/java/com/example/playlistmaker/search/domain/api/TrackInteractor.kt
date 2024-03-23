package com.example.playlistmaker.search.domain.api

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.domain.models.DataSongs
import java.util.function.Consumer

interface TrackInteractor {
    public val sharedPreferences: SharedPreferences
    fun searchTracks(expression: String, consumer: TracksConsumer)
    fun interface TracksConsumer {
        fun consume(foundTracks: List<DataSongs>?, errorMessage: Int?)
    }

    fun readFromSharedPreferences(): ArrayList<DataSongs>

    fun writeToSharedPreferences(trackList: ArrayList<DataSongs>)
}