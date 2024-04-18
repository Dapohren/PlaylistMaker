package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.domain.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.Impl.AudioPlayerInteractorImpl
import org.koin.dsl.module

val audioPlayerDomainModule = module {
    factory<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl(get())
    }
}