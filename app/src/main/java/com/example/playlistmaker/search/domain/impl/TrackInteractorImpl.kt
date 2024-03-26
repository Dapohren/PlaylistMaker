package com.example.playlistmaker.search.domain.impl

import android.content.SharedPreferences

import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.DataSongs
import com.example.playlistmaker.search.presentation.Resource
import com.example.playlistmaker.search.presentation.SONGS_LIST_KEY
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository,
                          override val sharedPreferences: SharedPreferences,
                          ) : TrackInteractor {

    private val executor = Executors.newCachedThreadPool()



    override fun searchTracks(expression: String, consumer: TrackInteractor.TracksConsumer) {
        executor.execute {
            when(val resource = repository.searchTracks(expression)) {
                is Resource.Success -> { consumer.consume(resource.data, null) }
                is Resource.Error -> { consumer.consume(null, resource.message) }
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