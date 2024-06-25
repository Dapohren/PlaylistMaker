package com.example.playlistmaker.media.domain.models

data class PlaylistModel (
    val playlistId: Int?,
    val playlistName: String,
    val playlistDescription: String?,
    val playlistImage: String?,
    var addedTracksId: ArrayList<Long>,
    var addedTracksNumber: Int,
)