package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.interfaces.playlist.PlaylistInteractor
import com.example.playlistmaker.media.domain.interfaces.playlist.PlaylistRepository
import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistsRepository: PlaylistRepository) : PlaylistInteractor {
    override fun getPlaylists(): Flow<List<PlaylistModel>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun putTrackInPlaylist(track: DataSongs, playlist: PlaylistModel) {
        playlistsRepository.putTrackInPlaylist(track, playlist)
    }

    override suspend fun createPlaylist(playlist: PlaylistModel) {
        playlistsRepository.createPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Int?) {
        playlistsRepository.deletePlaylist(playlistId)
    }
}