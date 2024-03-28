package com.example.playlistmaker.player.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.AudioPlayerRepository
import org.koin.dsl.module

val audioPlayerDataModule = module {
    factory <AudioPlayerRepository>{
        AudioPlayerRepositoryImpl(get())
    }
    factory{
        MediaPlayer()
    }
}