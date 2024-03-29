package com.example.playlistmaker.player.presentation

import com.example.playlistmaker.player.domain.model.States

sealed class AudioPlayerState {
    object NotReady: AudioPlayerState()
    object OnStart: AudioPlayerState()
    class Play(val currentPosition: String): AudioPlayerState()
    object Pause: AudioPlayerState()
    object Ready: AudioPlayerState()
}