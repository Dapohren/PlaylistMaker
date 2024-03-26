package com.example.playlistmaker.creator

import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl

/*object Creator {
    private fun getTrackRepository() : TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }
    fun provideMoviesInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }
}*/