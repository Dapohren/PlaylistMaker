package com.example.playlistmaker.player.presentation

sealed class FavouriteState {
    object Liked: FavouriteState()
    object NotLiked: FavouriteState()
}