package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.domain.models.DataSongs

class TrackRepositoryImpl (private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTracks(expression: String): List<DataSongs> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as TrackResponse).results.map {
                DataSongs(
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )}
        } else {
            return emptyList()
        }
    }
}