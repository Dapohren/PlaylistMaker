package com.example.playlistmaker.playlist.domain

import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.flow.Flow

interface PlaylistInformationRepository {
    suspend fun editPlaylist(playlistId: Int?, playlistName: String, playlistDescription: String, playlistImage: String?)
    suspend fun getPlaylist(playlistId: Int?): PlaylistModel
    suspend fun deleteTrackFromPlaylist(track: DataSongs, playlist: PlaylistModel)
    suspend fun getTracksInPlaylists(): List<DataSongs>
    suspend fun getTracksInPlaylistWithId(addedTracksId: ArrayList<Long>): ArrayList<DataSongs>
    fun getPlaylists(): Flow<List<PlaylistModel>>


}