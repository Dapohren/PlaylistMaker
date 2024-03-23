package com.example.playlistmaker.search.domain.api



import com.example.playlistmaker.search.domain.models.DataSongs

interface TrackRepository {
    fun searchTracks(expression: String) : com.example.playlistmaker.search.presentation.Resource<List<DataSongs>>
}