package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import org.koin.dsl.module

val searchDomainModule = module {
    factory<TrackInteractor> {
        TrackInteractorImpl(get(), get())
    }
}