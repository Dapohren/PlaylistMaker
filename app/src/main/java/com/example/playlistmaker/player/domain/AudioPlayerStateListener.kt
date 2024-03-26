package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.model.States

fun interface AudioPlayerStateListener {
    fun onStateChanged(state: PlayerState)
}