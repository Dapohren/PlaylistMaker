package com.example.playlistmaker.playlist.data

import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.db.PlaylistEntity
import com.example.playlistmaker.media.data.db.TrackDatabase
import com.example.playlistmaker.media.data.db.TracksForPlaylistEntity
import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.playlist.domain.PlaylistInformationRepository
import com.example.playlistmaker.search.domain.models.DataSongs

class PlaylistInformationRepositoryImpl(private val appDatabase: TrackDatabase,
                                        private val playlistDbConverter: PlaylistDbConverter) : PlaylistInformationRepository{
    override suspend fun editPlaylist(
        playlistId: Int?,
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?
    ) {
        appDatabase.playlistDao().editPlaylist(playlistId, playlistName, playlistDescription, playlistImage)
    }

    override suspend fun getPlaylist(playlistId: Int?): PlaylistModel {

        return convertFromPlaylistEntity(appDatabase.playlistDao().getPlaylistEntity(playlistId))
    }

    override suspend fun deleteTrackFromPlaylist(track: DataSongs, playlist: PlaylistModel) {
        val track = convertToTrackToPlaylistEntity(track)
        appDatabase.tracksForPlaylistDao().deleteTrackEntity(track)
    }

    override suspend fun getTracksInPlaylists(): List<DataSongs> {
        return appDatabase.tracksForPlaylistDao().getTracksInPlaylists().map { tracks -> convertToDataSongs(tracks) }
    }

    override suspend fun getTracksInPlaylistWithId(addedTracksId: ArrayList<Long>): ArrayList<DataSongs> {
        val tracksInPlaylists = getTracksInPlaylists()
        val tracksInPlaylist = arrayListOf<DataSongs>()
        tracksInPlaylists.forEach { if(addedTracksId.contains(it.trackId)) tracksInPlaylist.add(it) }
        return tracksInPlaylist
    }
    private fun convertToTrackToPlaylistEntity(track: DataSongs): TracksForPlaylistEntity {
        return playlistDbConverter.map(track)
    }
    private fun convertToDataSongs(track: TracksForPlaylistEntity): DataSongs {
        return playlistDbConverter.map(track)
    }
    private fun convertFromPlaylistEntity(playlists: PlaylistEntity): PlaylistModel{
        return playlistDbConverter.map(playlists)
    }
}