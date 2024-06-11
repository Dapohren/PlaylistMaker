package com.example.playlistmaker.media.presentation

import com.example.playlistmaker.media.domain.models.FavTracksModel

sealed class FavouriteStates {
    object Empty: FavouriteStates()
    class NotEmpty(val tracks: List<FavTracksModel>): FavouriteStates()
}