package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {
    private val baseURL = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val imdbService = retrofit.create(iTunesApi::class.java)
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