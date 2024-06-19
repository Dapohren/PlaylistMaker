package com.example.playlistmaker.media.presentation.playlist

import com.example.playlistmaker.media.domain.models.PlaylistModel

sealed class PlaylistState {
    class NotEmpty(val playlists: List<PlaylistModel>): PlaylistState()
    object Empty: PlaylistState()
}