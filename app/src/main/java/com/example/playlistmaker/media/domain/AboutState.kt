package com.example.playlistmaker.media.domain

sealed interface AboutState {
    data class Error(
        val message: String
    ) : AboutState
}