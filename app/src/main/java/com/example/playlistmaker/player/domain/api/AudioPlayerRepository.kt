package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.AudioPlayerStateListener
import com.example.playlistmaker.player.domain.model.States

interface AudioPlayerRepository {
    var listener: AudioPlayerStateListener?
    fun prepareAudio()

    fun playAudio()

    fun pauseAudio()

    fun releasePlayer()

    fun setDataSource(url: String)

    fun showCurrentPosition(): String
}
