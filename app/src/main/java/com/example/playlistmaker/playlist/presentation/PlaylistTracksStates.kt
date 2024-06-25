package com.example.playlistmaker.playlist.presentation

import com.example.playlistmaker.media.domain.models.FavTracksModel
import com.example.playlistmaker.media.presentation.FavouriteStates
import com.example.playlistmaker.search.domain.models.DataSongs

sealed class PlaylistTracksStates {
    object Empty: PlaylistTracksStates()
    class NotEmpty(val tracks: List<DataSongs>): PlaylistTracksStates()
}