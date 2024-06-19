package com.example.playlistmaker.media.data.impl

import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.db.PlaylistEntity
import com.example.playlistmaker.media.data.db.TrackDatabase
import com.example.playlistmaker.media.data.db.TracksForPlaylistEntity
import com.example.playlistmaker.media.domain.db.playlist.PlaylistRepository
import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: TrackDatabase,
    private val playlistDbConverter: PlaylistDbConverter,

    ): PlaylistRepository {
    override fun getPlaylists(): Flow<List<PlaylistModel>> {
        return appDatabase.playlistDao().getPlaylists().map { playlists -> convertFromPlaylistEntity(playlists) }
    }

    override suspend fun putTrackInPlaylist(track: DataSongs, playlist: PlaylistModel){
        val trackToPlaylistEntity = convertToTrackToPlaylistEntity(track)

        playlist.addedTracksId.add(track.trackId)
        playlist.addedTracksNumber++
        val newTracksId = playlist.addedTracksId.toString()
        val addedTracksNumber = playlist.addedTracksNumber
        val playlistId = playlist.playlistId

        appDatabase.playlistDao().changeTracksList(newTracksId, playlistId!!, addedTracksNumber)
        appDatabase.tracksForPlaylistDao().insertTrack(trackToPlaylistEntity)
    }

    override suspend fun createPlaylist(playlist: PlaylistModel) {
        val playlistEntity = convertToPlaylistEntity(playlist)
        appDatabase.playlistDao().addPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlistId: Int?) {
        appDatabase.playlistDao().deletePlaylist(playlistId)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<PlaylistModel>{
        return playlists.map{ playlist -> playlistDbConverter.map(playlist)}
    }

    private fun convertToPlaylistEntity(playlist: PlaylistModel): PlaylistEntity{
        return playlistDbConverter.map(playlist)
    }

    private fun convertToTrackToPlaylistEntity(track: DataSongs): TracksForPlaylistEntity{
        return playlistDbConverter.map(track)
    }
}