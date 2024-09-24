package com.example.playlistmaker.playlist.di

import com.example.playlistmaker.playlist.domain.PlaylistInformationInteractor
import com.example.playlistmaker.playlist.domain.PlaylistInformationInteractorImpl
import org.koin.dsl.module

val domainInfoModule = module {
    single<PlaylistInformationInteractor> {
        PlaylistInformationInteractorImpl(get())
    }
}