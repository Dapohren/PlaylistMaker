package com.example.playlistmaker.media.di

import androidx.room.Room
import com.example.playlistmaker.media.data.converters.TrackDbConverter
import com.example.playlistmaker.media.data.db.TrackDatabase
import com.example.playlistmaker.media.data.impl.FavTracksRepositoryImpl
import com.example.playlistmaker.media.domain.db.FavTracksInteractor
import com.example.playlistmaker.media.domain.db.FavTracksRepository
import com.example.playlistmaker.media.domain.impl.FavTracksInteractorImpl
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

}