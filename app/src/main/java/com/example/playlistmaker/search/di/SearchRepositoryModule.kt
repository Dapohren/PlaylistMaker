package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.data.network.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.TrackRepository
import org.koin.dsl.module

val searchRepositoryModule = module {
    factory<TrackRepository> {
        TrackRepositoryImpl(get())
    }
}