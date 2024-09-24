package com.example.playlistmaker.media.domain.interfaces

import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.DataSongs

class SharedPrefMediaInteractor(private val sharedPref: TrackInteractor) {
    fun readFromSharedPreferences(): ArrayList<DataSongs>{
        return sharedPref.readFromSharedPreferences()
    }

    fun writeToSharedPreferences(trackList: ArrayList<DataSongs>) {
        sharedPref.writeToSharedPreferences(trackList)
    }
}