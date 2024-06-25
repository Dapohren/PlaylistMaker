package com.example.playlistmaker.playlist.domain

import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.search.domain.models.DataSongs

class PlaylistInformationInteractorImpl(private val repository: PlaylistInformationRepository) : PlaylistInformationInteractor{
    override suspend fun editPlaylist(
        playlistId: Int?,
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?
    ) {
        repository.editPlaylist(playlistId = playlistId, playlistName = playlistName, playlistDescription = playlistDescription, playlistImage = playlistImage)
    }

    override suspend fun getPlaylist(playlistId: Int?): PlaylistModel {
        return repository.getPlaylist(playlistId = playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(track: DataSongs, playlist: PlaylistModel) {
        repository.deleteTrackFromPlaylist(track = track, playlist = playlist)
    }



    override suspend fun getTracksInPlaylistWithId(addedTracksId: ArrayList<Long>): ArrayList<DataSongs> {
        return repository.getTracksInPlaylistWithId(addedTracksId = addedTracksId)
    }
}