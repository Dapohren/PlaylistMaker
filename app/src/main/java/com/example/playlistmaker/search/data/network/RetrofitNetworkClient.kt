package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest



class RetrofitNetworkClient(private val imdbService: iTunesApi) : NetworkClient {

    override fun doRequest(request: TrackSearchRequest): Response {
        return try {
            val resp = imdbService.search(request.expression).execute()
            val body = resp.body() ?: Response()
            body.apply {
                resultCode = resp.code()
            }
        } catch (exception: Exception) {
            Response().apply {
                resultCode = 400
            }

        }
    }
}