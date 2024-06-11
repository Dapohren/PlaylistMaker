package com.example.playlistmaker.player.presentation


import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.Impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.model.States
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AudioPlayerViewModel(private val audioPlayerInteractor: AudioPlayerInteractor) : ViewModel() {
    private var isPlayerUsed = false
    private var isPlayerPrepared = false
    private val _state = MutableLiveData<AudioPlayerState>()
    val state: LiveData<AudioPlayerState> = _state
    private var timerJob: Job? = null
    private var mediaPlayer: MediaPlayer = MediaPlayer()

    init {
        _state.postValue(AudioPlayerState.NotReady)

    }

    fun startPreparingPlayer(url: String){
        if(!isPlayerPrepared)
            preparePlayer(url)
    }


    fun playbackControl(){
        if(audioPlayerInteractor.getCurrentState() == States.STATE_PLAYING) {
            onPauseButtonClicked()
        }
        else{
            onPlayButtonClicked()
        }
    }


    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }



    fun onPlayButtonClicked() {
        startPlayer()
        _state.postValue(AudioPlayerState.Play(showPlayerCurrentPosition()))
    }

    fun onPauseButtonClicked() {
        pausePlayer()
        _state.postValue(AudioPlayerState.Pause)
    }
    fun startPlayer() {
        audioPlayerInteractor.playAudio()
        startTimer()
        isPlayerUsed = true
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (audioPlayerInteractor.isPlaying()) {
                delay(DELAY)
                _state.postValue(AudioPlayerState.Play(showPlayerCurrentPosition()))
            }
        }
    }
    fun pausePlayer() {
        _state.postValue(AudioPlayerState.Pause)
        audioPlayerInteractor.pauseAudio()
        timerJob?.cancel()
    }

    private fun releasePlayer() {
        audioPlayerInteractor.unSubscribeOnPlayer()
        audioPlayerInteractor.releasePlayer()
        timerJob?.cancel()
    }

    private fun preparePlayer(url: String) {
        audioPlayerInteractor.setDataSource(url)
        audioPlayerInteractor.prepareAudio()
        isPlayerPrepared = true

        audioPlayerInteractor.subscribeOnPlayer { state ->
            when (state) {
                PlayerState.NOT_READY -> {}
                PlayerState.PREPARED -> {
                    _state.postValue(AudioPlayerState.Ready)
                }

                PlayerState.COMPLETE -> {
                    _state.postValue(AudioPlayerState.OnStart)
                    timerJob?.cancel()
                }
            }
        }
    }

    fun showPlayerCurrentPosition(): String {
        return audioPlayerInteractor.showCurrentPosition()
    }

    companion object {
        private const val DELAY = 300L
    }
}