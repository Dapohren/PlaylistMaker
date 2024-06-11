package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.domain.model.States
import com.example.playlistmaker.player.presentation.FavouriteState

interface AudioPlayerInteractor {

    fun prepareAudio()

    fun playAudio()

    fun pauseAudio()

    fun releasePlayer()

    fun getCurrentState() : States

    fun getCurrentFavState() : FavouriteState

    fun subscribeOnPlayer(listener: AudioPlayerStateListener)

    fun unSubscribeOnPlayer()

    fun setDataSource(url: String)

    fun showCurrentPosition(): String

    fun isPlaying(): Boolean


}