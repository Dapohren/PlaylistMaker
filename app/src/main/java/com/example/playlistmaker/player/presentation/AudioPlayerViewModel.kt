package com.example.playlistmaker.player.presentation


import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.model.States


class AudioPlayerViewModel(private val audioPlayerInteractor: AudioPlayerInteractor) : ViewModel() {
    private var isPlayerUsed = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timePassedRunnable: Runnable

    private val _state = MutableLiveData<AudioPlayerState>()
    val state: LiveData<AudioPlayerState> = _state

    init {
        _state.postValue(AudioPlayerState.NotReady)
        preparePlayer()
    }


    override fun onCleared() {
        releasePlayer()
    }

    fun playbackControl(){
        if(audioPlayerInteractor.getCurrentState() == States.STATE_PLAYING) {
            onPauseButtonClicked()
        }
        else{
            handler.removeCallbacks(createTimePassedTask())
            onPlayButtonClicked()
        }
    }

    fun onPlayButtonClicked() {
        startPlayer()
        _state.postValue(AudioPlayerState.Play(showPlayerCurrentPosition()))
    }

    fun onPauseButtonClicked() {
        pausePlayer()
        _state.postValue(AudioPlayerState.Pause)
    }
    private fun startPlayer() {
        audioPlayerInteractor.playAudio()
        if (!isPlayerUsed) {
            timePassedRunnable = createTimePassedTask()
        }
        handler.post(timePassedRunnable)
        isPlayerUsed = true
    }

    fun pausePlayer() {
        _state.postValue(AudioPlayerState.Pause)
        audioPlayerInteractor.pauseAudio()
        handlerRemoveCallbacks()
    }

    private fun releasePlayer() {
        audioPlayerInteractor.unSubscribeOnPlayer()
        audioPlayerInteractor.releasePlayer()
        handlerRemoveCallbacks()
    }

    private fun preparePlayer() {
        audioPlayerInteractor.setDataSource()
        audioPlayerInteractor.prepareAudio()

        audioPlayerInteractor.subscribeOnPlayer { state ->
            when (state) {
                PlayerState.NOT_READY -> {}
                PlayerState.PREPARED -> {
                    _state.postValue(AudioPlayerState.Ready)
                }

                PlayerState.COMPLETE -> {
                    _state.postValue(AudioPlayerState.OnStart)
                    handlerRemoveCallbacks()
                }
            }
        }
    }

    fun showPlayerCurrentPosition(): String {
        return audioPlayerInteractor.showCurrentPosition()
    }

    private fun createTimePassedTask(): Runnable {
        return object : Runnable {
            override fun run() {
                _state.postValue(AudioPlayerState.Play(showPlayerCurrentPosition()))
                handler.postDelayed(this, DELAY)
            }
        }
    }



    private fun handlerRemoveCallbacks() {
        if (isPlayerUsed) {
            handler.removeCallbacks(timePassedRunnable)
        }
    }

    companion object {
        private const val DELAY = 1000L
    }
}