package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.PlaylistEntity
import com.example.playlistmaker.media.data.db.TracksForPlaylistEntity
import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.search.domain.models.DataSongs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter {
    fun map(playlist: PlaylistModel) : PlaylistEntity {
        return PlaylistEntity(playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.playlistImage,
            Gson().toJson(playlist.addedTracksId),
            playlist.addedTracksNumber,)
    }

    fun map(playlistEntity: PlaylistEntity): PlaylistModel{
        val type = object: TypeToken<ArrayList<Long>>() {}.type
        return PlaylistModel(
            playlistEntity.playlistId,
            playlistEntity.playlistName,
            playlistEntity.playlistDescription,
            playlistEntity.playlistImage,
            Gson().fromJson(playlistEntity.addedTracksId, type),
            playlistEntity.addedTracksNumber,
        )
    }

    fun map(track: DataSongs): TracksForPlaylistEntity {
        return TracksForPlaylistEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }
}