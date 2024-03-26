package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.AudioPlayerStateListener

interface AudioPlayerRepository {
    var listener: AudioPlayerStateListener?
    fun prepareAudio()

    fun playAudio()

    fun pauseAudio()

    fun releasePlayer()

    fun setDataSource()

    fun showCurrentPosition(): String
}
