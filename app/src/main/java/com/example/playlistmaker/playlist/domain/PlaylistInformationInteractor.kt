package com.example.playlistmaker.playlist.domain

import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.search.domain.models.DataSongs

interface PlaylistInformationInteractor {

    suspend fun editPlaylist(playlistId: Int?, playlistName: String, playlistDescription: String, playlistImage: String?)
    suspend fun getPlaylist(playlistId: Int?): PlaylistModel
    suspend fun deleteTrackFromPlaylist(track: DataSongs, playlist: PlaylistModel)
    suspend fun getTracksInPlaylistWithId(addedTracksId: ArrayList<Long>): ArrayList<DataSongs>

}