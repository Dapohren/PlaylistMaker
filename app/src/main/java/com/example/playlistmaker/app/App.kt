package com.example.playlistmaker.app

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.media.di.dataModule
import com.example.playlistmaker.media.di.mediaViewModelModule
import com.example.playlistmaker.player.di.audioPlayerDataModule
import com.example.playlistmaker.player.di.audioPlayerDomainModule
import com.example.playlistmaker.player.di.audioPlayerViewModelModule
import com.example.playlistmaker.search.di.searchDataModule
import com.example.playlistmaker.search.di.searchDomainModule
import com.example.playlistmaker.search.di.searchRepositoryModule
import com.example.playlistmaker.search.di.searchViewModelModule
import com.example.playlistmaker.settings.di.data.settingsDataModule
import com.example.playlistmaker.settings.di.data.settingsRepositoryModule
import com.example.playlistmaker.settings.di.domain.settingsDomainModule
import com.example.playlistmaker.settings.di.view.settingsViewModelModule
import com.markodevcic.peko.PermissionRequester
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import sharedPrefInteractorModule

class App : Application() {
    private lateinit var sharedPref: SharedPreferences
    private var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        PermissionRequester.initialize(applicationContext)
        sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPref.getBoolean(KEY, false)
        switchTheme(darkTheme)

        startKoin{
            androidContext(this@App)
            modules (
                searchDataModule, searchRepositoryModule, searchDomainModule, searchViewModelModule,
                audioPlayerDataModule, audioPlayerDomainModule, audioPlayerViewModelModule,
                settingsDataModule, settingsRepositoryModule, settingsDomainModule, settingsViewModelModule,
                mediaViewModelModule, dataModule, sharedPrefInteractorModule
            )
        }


    }
    private fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object{
        const val PREFERENCES = "pref"
        const val KEY = "key_theme"
    }
}