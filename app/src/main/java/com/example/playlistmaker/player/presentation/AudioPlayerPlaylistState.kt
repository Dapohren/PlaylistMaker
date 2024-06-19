package com.example.playlistmaker.player.presentation

import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.search.domain.models.DataSongs

sealed class AudioPlayerPlaylistState {
    class InPlaylist(val playlistName: String): AudioPlayerPlaylistState()
    class NotInPlaylist(val track: DataSongs, val playlist: PlaylistModel): AudioPlayerPlaylistState()
    class AddedToPlaylist(val playlistName: String): AudioPlayerPlaylistState()
}