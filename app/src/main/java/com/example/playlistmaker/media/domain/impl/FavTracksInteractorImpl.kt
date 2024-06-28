package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.interfaces.FavTracksInteractor
import com.example.playlistmaker.media.domain.interfaces.FavTracksRepository
import com.example.playlistmaker.media.domain.models.FavTracksModel
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.flow.Flow

class FavTracksInteractorImpl(private val repository: FavTracksRepository) : FavTracksInteractor {
    override suspend fun addTracksToFav(trackId: DataSongs) {
        repository.addTrackToFav(trackId)
    }

    override suspend fun deleteTrackFromFav(trackId: DataSongs) {
        repository.deleteTrackFromFav(trackId)
    }

    override fun getAllTracksFromFav(): Flow<List<FavTracksModel>> {
        return repository.getAllFavTracks()
    }

    override fun getTrackId(): Flow<List<Long>> {
        return repository.getFavTrackId()
    }

}