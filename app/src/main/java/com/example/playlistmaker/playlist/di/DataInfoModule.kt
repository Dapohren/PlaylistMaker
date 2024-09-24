package com.example.playlistmaker.playlist.di

import com.example.playlistmaker.playlist.data.PlaylistInformationRepositoryImpl
import com.example.playlistmaker.playlist.domain.PlaylistInformationRepository
import org.koin.dsl.module

val dataInfoModule = module {
    single<PlaylistInformationRepository> {
        PlaylistInformationRepositoryImpl(get(), get())
    }
}