package com.example.playlistmaker.player.presentation

sealed class AudioPlayerState {
    object NotReady: AudioPlayerState()
    object Ready: AudioPlayerState()
    object OnStart: AudioPlayerState()
    class Play(val currentPosition: String): AudioPlayerState()
    object Pause: AudioPlayerState()
}