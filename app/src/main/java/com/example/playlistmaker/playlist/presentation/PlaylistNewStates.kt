package com.example.playlistmaker.playlist.presentation

import com.example.playlistmaker.media.domain.models.PlaylistModel

sealed class PlaylistNewStates {
    class Content(val playlist: PlaylistModel): PlaylistNewStates()
}