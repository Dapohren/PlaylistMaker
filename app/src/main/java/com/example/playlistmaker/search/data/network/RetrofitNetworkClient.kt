package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest


class RetrofitNetworkClient(private val imdbService: iTunesApi) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        return if (dto is TrackSearchRequest) {
            val resp = imdbService.search(dto.expression).execute()

            val body = resp.body() ?: Response()

            body.apply { resultCode = resp.code() }
        } else {
            Response().apply { resultCode = 400 }
        }

    }
}