package com.example.playlistmaker.search.di

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.app.App
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TrackRepositoryImpl
import com.example.playlistmaker.search.data.network.iTunesApi
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.presentation.SONGS_PREFERENCES
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchDataModule = module {
    single <iTunesApi>{
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(
                SONGS_PREFERENCES,
                AppCompatActivity.MODE_PRIVATE)
    }
    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }
}