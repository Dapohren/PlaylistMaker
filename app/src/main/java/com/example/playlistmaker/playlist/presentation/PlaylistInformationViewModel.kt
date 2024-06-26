package com.example.playlistmaker.playlist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.SharedPrefMediaInteractor
import com.example.playlistmaker.media.domain.db.playlist.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.media.presentation.FavouriteStates
import com.example.playlistmaker.player.presentation.AudioPlayerPlaylistState
import com.example.playlistmaker.playlist.domain.PlaylistInformationInteractor
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistInformationViewModel(private val interactorInf: PlaylistInformationInteractor, private val interactorPlay: PlaylistInteractor, private val sharedPrefMediaInteractor: SharedPrefMediaInteractor) : ViewModel() {

    private val _state = MutableLiveData<PlaylistTracksStates>()
    val state: LiveData<PlaylistTracksStates> = _state

    private val _states = MutableLiveData<PlaylistNewStates>()
    val states: LiveData<PlaylistNewStates> = _states


    fun getPlaylistById(playlistId: Int?){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                val chosenPlaylist = interactorInf.getPlaylist(playlistId)
                _states.postValue(PlaylistNewStates.Content(chosenPlaylist))
            }
        }
    }

    fun deleteTrackFromPlaylist(trackId: DataSongs, playlist: PlaylistModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                interactorInf.deleteTrackFromPlaylist(trackId, playlist)
            }
        }
    }

    fun getTracksFromPlaylist(addedTracksId: ArrayList<Long>)  {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
               val inter = interactorInf
                    .getTracksInPlaylistWithId(addedTracksId)

                if(inter.isEmpty())
                    _state.postValue(PlaylistTracksStates.Empty)
                else
                    _state.postValue(PlaylistTracksStates.NotEmpty(inter))
            }
            }
        }
    fun editPlaylist(playlistId: Int?, playlistName: String, playlistDescription: String, playlistImage: String?){
        viewModelScope.launch{
            withContext(Dispatchers.IO) {
                interactorInf.editPlaylist(
                    playlistId,
                    playlistName,
                    playlistDescription,
                    playlistImage
                )
            }
        }
    }

    fun deletePlaylist(playlistId: Int?){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                interactorPlay.deletePlaylist(playlistId)
            }
        }

    }
    fun readFromSharedPreferences(): ArrayList<DataSongs>{
        return sharedPrefMediaInteractor.readFromSharedPreferences()
    }

    fun writeToSharedPreferences(trackList: ArrayList<DataSongs>){
        sharedPrefMediaInteractor.writeToSharedPreferences(trackList)
    }

}
