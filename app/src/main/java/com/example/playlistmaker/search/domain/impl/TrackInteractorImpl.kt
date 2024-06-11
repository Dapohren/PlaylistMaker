package com.example.playlistmaker.search.domain.impl

import android.content.SharedPreferences

import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.DataSongs
import com.example.playlistmaker.search.presentation.Resource
import com.example.playlistmaker.search.ui.SONGS_LIST_KEY
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository,
                          private val sharedPreferences: SharedPreferences,
                          ) : TrackInteractor {

    override fun searchTracks(expression: String) : Flow<Pair<List<DataSongs>?, Int?>> {
        return repository.searchTracks(expression).map { result ->
            when(result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun readFromSharedPreferences(): ArrayList<DataSongs> {
        val songsSh = sharedPreferences.getString(SONGS_LIST_KEY, null) ?: return ArrayList()
        return Gson().fromJson(songsSh, object : TypeToken<ArrayList<DataSongs>>() {}
            .type)
    }

    override fun writeToSharedPreferences(trackList: ArrayList<DataSongs>) {
        sharedPreferences.edit()
            .putString(SONGS_LIST_KEY, Gson().toJson(trackList))
            .apply()
    }

}