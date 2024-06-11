package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.TrackEntity
import com.example.playlistmaker.media.domain.models.FavTracksModel
import com.example.playlistmaker.search.domain.models.DataSongs

class TrackDbConverter {
    fun map(track: DataSongs) : TrackEntity{
        return TrackEntity(track.trackId,track.trackName,track.artistName,track.trackTimeMillis,track.artworkUrl100,track.collectionName,track.releaseDate,track.primaryGenreName,track.country,track.previewUrl)
    }
    fun map(entity: TrackEntity) : FavTracksModel{
        return FavTracksModel(entity.id,entity.trackName,entity.artistName,entity.trackTimeMillis,entity.artworkUrl100,entity.collectionName,entity.releaseDate,entity.primaryGenreName,entity.country,entity.previewUrl)
    }
}