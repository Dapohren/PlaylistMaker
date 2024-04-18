package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.domain.model.PlayerState

fun interface AudioPlayerStateListener {
    fun onStateChanged(state: PlayerState)
}