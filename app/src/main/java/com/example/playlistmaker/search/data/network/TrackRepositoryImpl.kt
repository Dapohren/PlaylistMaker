package com.example.playlistmaker.search.data.network



import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.domain.models.DataSongs
import com.example.playlistmaker.search.presentation.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class TrackRepositoryImpl (private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<DataSongs>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(response.resultCode))
            }
            200 -> {
                with(response as TrackResponse) {
                    val data = results.map {

                        DataSongs(
                            it.trackId,
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
                    emit(Resource.Success(data))
                }
            }
            else -> {
                emit(Resource.Error(response.resultCode))
            }
        }

    }
}
