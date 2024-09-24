package com.example.playlistmaker.media.domain.interfaces

import com.example.playlistmaker.media.domain.models.FavTracksModel
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.flow.Flow

interface FavTracksRepository {
    suspend fun addTrackToFav(trackId: DataSongs)
    suspend fun deleteTrackFromFav(trackId: DataSongs)
    fun getAllFavTracks() : Flow<List<FavTracksModel>>
    fun getFavTrackId() : Flow<List<Long>>
}