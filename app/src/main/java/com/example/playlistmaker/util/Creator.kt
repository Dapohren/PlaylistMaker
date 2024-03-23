package com.example.playlistmaker.util

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.player.data.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.Impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.player.domain.api.AudioPlayerRepository
import com.example.playlistmaker.player.presentation.AudioPlayerViewModelFactory
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.settings.data.InternetAccess
import com.example.playlistmaker.settings.data.Impl.InternetAccessImpl
import com.example.playlistmaker.settings.data.Impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.Impl.SharingInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.Impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.SharingInteractor

object Creator {

    //Settings
    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(internetAccess = provideInternetAccess(context = context))
    }
    fun provideSettingsInteractor(context: Context) : SettingsInteractor {
        return SettingsInteractorImpl(settingsRepository = provideSettingsRepository(context = context))
    }

    private fun provideSettingsRepository(context: Context) : SettingsRepository {
        return SettingsRepositoryImpl(context = context)
    }

    private fun provideInternetAccess(context: Context) : InternetAccess {
        return InternetAccessImpl(context = context)
    }

    //TrackSearch

    private fun getTrackRepository() : TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(sharedPreferences: SharedPreferences): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(), sharedPreferences)
    }

    //Player

    private fun getAudioPlayerRepository(url: String) : AudioPlayerRepository {
        return AudioPlayerRepositoryImpl(url = url)
    }

    private fun provideAudioPlayerInteractor(url: String): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioPlayerRepository(url = url))
    }

    fun provideAudioPlayerViewModelFactory(url: String): AudioPlayerViewModelFactory {
        return AudioPlayerViewModelFactory(provideAudioPlayerInteractor(url = url))
    }

}