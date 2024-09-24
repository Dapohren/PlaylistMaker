package com.example.playlistmaker.playlist.data

import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.db.PlaylistEntity
import com.example.playlistmaker.media.data.db.TrackDatabase
import com.example.playlistmaker.media.data.db.TracksForPlaylistEntity
import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.playlist.domain.PlaylistInformationRepository
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistInformationRepositoryImpl(private val appDatabase: TrackDatabase,
                                        private val playlistDbConverter: PlaylistDbConverter) : PlaylistInformationRepository{
    override suspend fun editPlaylist(
        playlistId: Int?,
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?
    ) {
        appDatabase.playlistDao().editPlaylist(playlistId, playlistName, playlistDescription, playlistImage)
    }

    override suspend fun getPlaylist(playlistId: Int?): PlaylistModel {

        return convertFromPlaylistEntity(appDatabase.playlistDao().getPlaylistEntity(playlistId))
    }
    override fun getPlaylists(): Flow<List<PlaylistModel>> {
        return appDatabase.playlistDao().getPlaylists().map { playlists -> convertFromListPlaylistEntityToListPlaylist(playlists) }
    }

    override suspend fun deleteTrackFromPlaylist(track: DataSongs, playlist: PlaylistModel) {
        playlist.addedTracksId.remove(track.trackId)
        playlist.addedTracksNumber--
        if(playlist.addedTracksNumber <= 0) {
            playlist.addedTracksNumber = 0
        }
        val newTracksId = playlist.addedTracksId.toString()
        val addedTracksNumber = playlist.addedTracksNumber
        val playlistId = playlist.playlistId
        appDatabase.playlistDao().changeTracksList(newTracksId, playlistId!!, addedTracksNumber)

        getPlaylists().collect{
            checkTrackInPlaylists(track, it)
        }
    }

    override suspend fun getTracksInPlaylists(): List<DataSongs> {
        return appDatabase.tracksForPlaylistDao().getTracksInPlaylists().map { tracks -> convertToDataSongs(tracks) }
    }
    private fun checkTrackInPlaylists(track: DataSongs, playlists: List<PlaylistModel>){
        var check = 0
        playlists.forEach {
            if(it.addedTracksId.contains(track.trackId))
                check++
        }
        if(check == 0){
            val chosenTrackEntity = convertFromTrackDomainMediaLibraryToTrackToPlaylistEntity(track)
            appDatabase.tracksForPlaylistDao().deleteTrackEntity(chosenTrackEntity)
        }
    }

    override suspend fun getTracksInPlaylistWithId(addedTracksId: ArrayList<Long>): ArrayList<DataSongs> {
        val tracksInPlaylists = getTracksInPlaylists()
        val tracksInPlaylist = arrayListOf<DataSongs>()
        tracksInPlaylists.forEach { if(addedTracksId.contains(it.trackId)) tracksInPlaylist.add(it) }
        return tracksInPlaylist
    }
    private fun convertToTrackToPlaylistEntity(track: DataSongs): TracksForPlaylistEntity {
        return playlistDbConverter.map(track)
    }
    private fun convertFromTrackDomainMediaLibraryToTrackToPlaylistEntity(track: DataSongs): TracksForPlaylistEntity{
        return playlistDbConverter.map(track)
    }
    private fun convertToDataSongs(track: TracksForPlaylistEntity): DataSongs {
        return playlistDbConverter.map(track)
    }
    private fun convertFromListPlaylistEntityToListPlaylist(playlists: List<PlaylistEntity>): List<PlaylistModel>{
        return playlists.map{ playlist -> playlistDbConverter.map(playlist)}
    }
    private fun convertFromPlaylistEntity(playlists: PlaylistEntity): PlaylistModel{
        return playlistDbConverter.map(playlists)
    }
}