package com.example.playlistmaker.media.domain.interfaces.playlist

import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun getPlaylists(): Flow<List<PlaylistModel>>
    suspend fun putTrackInPlaylist(track: DataSongs, playlist: PlaylistModel)
    suspend fun createPlaylist(playlist: PlaylistModel)
    suspend fun deletePlaylist(playlistId: Int?)
}