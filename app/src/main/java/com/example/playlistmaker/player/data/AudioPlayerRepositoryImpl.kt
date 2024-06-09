package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.AudioPlayerStateListener
import com.example.playlistmaker.player.domain.api.AudioPlayerRepository
import com.example.playlistmaker.player.domain.model.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : AudioPlayerRepository {


    override var listener: AudioPlayerStateListener? = null
    override fun prepareAudio() {
        mediaPlayer.prepareAsync()
    }

    init{
        listener?.onStateChanged(PlayerState.NOT_READY)
        mediaPlayer.setOnPreparedListener{
            listener?.onStateChanged(PlayerState.PREPARED)
        }
        mediaPlayer.setOnCompletionListener{
            listener?.onStateChanged(PlayerState.COMPLETE)
        }

    }

    override fun playAudio() {
        mediaPlayer.start()

    }

    override fun pauseAudio(){
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun setDataSource(url: String) {
        mediaPlayer.setDataSource(url)
    }

    override fun showCurrentPosition(): String {
        if(!isPlaying()) return "00:00"

        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }
}