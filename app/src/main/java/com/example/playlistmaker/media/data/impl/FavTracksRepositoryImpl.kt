package com.example.playlistmaker.media.data.impl

import com.example.playlistmaker.media.data.converters.TrackDbConverter
import com.example.playlistmaker.media.data.db.TrackDatabase
import com.example.playlistmaker.media.data.db.TrackEntity
import com.example.playlistmaker.media.domain.interfaces.FavTracksRepository
import com.example.playlistmaker.media.domain.models.FavTracksModel
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavTracksRepositoryImpl(private val trackDatabase: TrackDatabase, private val converter: TrackDbConverter) :
    FavTracksRepository {
    override suspend fun addTrackToFav(trackId: DataSongs) {
        val trackEntity = convertToTrackEntity(trackId)
        trackDatabase.trackDao().addTrackToFav(trackEntity)
    }

    override fun getFavTrackId(): Flow<List<Long>> = flow{
        val trackId = trackDatabase.trackDao().getAllIdTracks()
        emit(trackId)
    }

    override suspend fun deleteTrackFromFav(trackId: DataSongs) {
        val trackEntity = convertToTrackEntity(trackId)
        trackDatabase.trackDao().deleteTrackFromFav(trackEntity)
    }

    override fun getAllFavTracks(): Flow<List<FavTracksModel>> {
        return trackDatabase.trackDao().getAllTracksFromFav().map{ track ->  convertFromTrackEntity(track)}
    }
    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<FavTracksModel> {
        return tracks.map { track -> converter.map(track) }
    }


    private fun convertToTrackEntity(track: DataSongs): TrackEntity {
        return converter.map(track)
    }

}