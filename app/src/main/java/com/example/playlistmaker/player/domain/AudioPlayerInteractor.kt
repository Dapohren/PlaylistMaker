package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.domain.model.States

interface AudioPlayerInteractor {

    fun prepareAudio()

    fun playAudio()

    fun pauseAudio()

    fun releasePlayer()

    fun getCurrentState() : States

    fun subscribeOnPlayer(listener: AudioPlayerStateListener)

    fun unSubscribeOnPlayer()

    fun setDataSource()

    fun showCurrentPosition(): String

}