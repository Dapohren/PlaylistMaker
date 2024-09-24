package com.example.playlistmaker.player.presentation


import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interfaces.FavTracksInteractor
import com.example.playlistmaker.media.domain.interfaces.playlist.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.media.presentation.playlist.PlaylistState
import com.example.playlistmaker.player.domain.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.model.States
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AudioPlayerViewModel(private val audioPlayerInteractor: AudioPlayerInteractor, private val favTracksInteractor: FavTracksInteractor, private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private var isPlayerUsed = false
    private var isPlayerPrepared = false
    private val _state = MutableLiveData<AudioPlayerState>()
    val state: LiveData<AudioPlayerState> = _state
    private var timerJob: Job? = null
    private var mediaPlayer: MediaPlayer = MediaPlayer()


    private val _favourites = MutableLiveData<FavouriteState>()
    val favourites: LiveData<FavouriteState> = _favourites
    private lateinit var favouriteTracksId: List<Long>

    private val _audioPlayerPlaylistState = MutableLiveData<AudioPlayerPlaylistState>()
    val audioPlayerPlaylistState: LiveData<AudioPlayerPlaylistState> = _audioPlayerPlaylistState

    private val _playlistState = MutableLiveData<PlaylistState>()
    val playlistState: LiveData<PlaylistState> = _playlistState


    fun isFavouriteClick(trackId: DataSongs) {
        viewModelScope.launch{
            withContext(Dispatchers.IO) {
                favTracksInteractor
                    .getTrackId()
                    .collect(){
                        favouriteTracksId = it
                    }
            }
            if(favouriteTracksId.contains(trackId.trackId)) {
                _favourites.postValue(FavouriteState.Liked)
            } else {
                _favourites.postValue(FavouriteState.NotLiked)
            }
        }
    }

    fun addToFavourite(trackId: DataSongs) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                favTracksInteractor.addTracksToFav(trackId)
            }
        }
        _favourites.postValue(FavouriteState.Liked)
    }

    fun deleteFromFav(trackId: DataSongs) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                favTracksInteractor.deleteTrackFromFav(trackId)
            }
        }
        _favourites.postValue(FavouriteState.NotLiked)
    }

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
    fun getPlaylists(){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                playlistInteractor
                    .getPlaylists()
                    .collect {
                        if(it.isEmpty())
                            _playlistState.postValue(PlaylistState.Empty)
                        else
                            _playlistState.postValue(PlaylistState.NotEmpty(it))
                    }
            }
        }
    }

    fun stateTrackInPlaylist(chosenTrack: DataSongs, chosenPlaylist: PlaylistModel){
        if(chosenPlaylist.addedTracksId.contains(chosenTrack.trackId))
            _audioPlayerPlaylistState.postValue(AudioPlayerPlaylistState.InPlaylist(chosenPlaylist.playlistName))
        else
            _audioPlayerPlaylistState.postValue(AudioPlayerPlaylistState.NotInPlaylist(chosenTrack, chosenPlaylist))
    }

    fun addTrackToPlaylist(track: DataSongs, chosenPlaylist: PlaylistModel){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                playlistInteractor.putTrackInPlaylist(track, chosenPlaylist)
                _audioPlayerPlaylistState.postValue(AudioPlayerPlaylistState.AddedToPlaylist(chosenPlaylist.playlistName))
            }
        }
    }


    companion object {
        private const val DELAY = 300L
    }
}