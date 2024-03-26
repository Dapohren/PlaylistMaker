package com.example.playlistmaker.player.domain.Impl

import com.example.playlistmaker.player.domain.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.AudioPlayerStateListener
import com.example.playlistmaker.player.domain.api.AudioPlayerRepository
import com.example.playlistmaker.player.domain.model.States
import com.example.playlistmaker.player.ui.AudioPlayerActivity

class AudioPlayerInteractorImpl(private val audioPlayerRepository: AudioPlayerRepository) : AudioPlayerInteractor {

    private var state = States.STATE_DEFAULT
    override fun prepareAudio() {
        audioPlayerRepository.prepareAudio()
        state = States.STATE_PREPARED

    }

    override fun playAudio() {
        audioPlayerRepository.playAudio()
        state = States.STATE_PLAYING
    }

    override fun pauseAudio() {
        audioPlayerRepository.pauseAudio()
        state = States.STATE_PAUSED

    }

    override fun releasePlayer() {
        audioPlayerRepository.releasePlayer()
        state = States.STATE_DEFAULT
    }

    override fun getCurrentState(): States = state

    override fun subscribeOnPlayer(listener: AudioPlayerStateListener) {
        audioPlayerRepository.listener = listener
    }

    override fun unSubscribeOnPlayer() {
        audioPlayerRepository.listener = null
    }

    override fun setDataSource() {
        audioPlayerRepository.setDataSource()
    }


    override fun showCurrentPosition(): String = audioPlayerRepository.showCurrentPosition()
}