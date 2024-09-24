package com.example.playlistmaker.media.domain.interfaces

import com.example.playlistmaker.media.domain.models.FavTracksModel
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.flow.Flow

interface FavTracksInteractor {
    suspend fun addTracksToFav(trackId: DataSongs)
    suspend fun deleteTrackFromFav(trackId: DataSongs)
    fun getAllTracksFromFav() : Flow<List<FavTracksModel>>
    fun getTrackId() : Flow<List<Long>>
}