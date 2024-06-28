package com.example.playlistmaker.media.di

import androidx.room.Room
import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.converters.TrackDbConverter
import com.example.playlistmaker.media.data.db.TrackDatabase
import com.example.playlistmaker.media.data.impl.FavTracksRepositoryImpl
import com.example.playlistmaker.media.data.impl.PlaylistRepositoryImpl
import com.example.playlistmaker.media.domain.interfaces.FavTracksInteractor
import com.example.playlistmaker.media.domain.interfaces.FavTracksRepository
import com.example.playlistmaker.media.domain.interfaces.playlist.PlaylistInteractor
import com.example.playlistmaker.media.domain.interfaces.playlist.PlaylistRepository
import com.example.playlistmaker.media.domain.impl.FavTracksInteractorImpl
import com.example.playlistmaker.media.domain.impl.PlaylistInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(androidContext(), TrackDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    factory { TrackDbConverter() }

    single<FavTracksRepository> {
        FavTracksRepositoryImpl(get(), get())
    }

    single<FavTracksInteractor> {
        FavTracksInteractorImpl(get())
    }

    factory { PlaylistDbConverter() }

    single<PlaylistRepository>{
        PlaylistRepositoryImpl(get(), get())
    }

    single<PlaylistInteractor>{
        PlaylistInteractorImpl(get())
    }

}