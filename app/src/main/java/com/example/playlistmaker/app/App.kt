package com.example.playlistmaker.app

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.di.data.settingsDataModule
import com.example.playlistmaker.settings.di.data.settingsRepositoryModule
import com.example.playlistmaker.settings.di.domain.settingsDomainModule
import com.example.playlistmaker.settings.di.view.settingsViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    private lateinit var sharedPref: SharedPreferences
    private var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPref.getBoolean(KEY, false)
        switchTheme(darkTheme)

        startKoin{
            androidContext(this@App)
            modules (
                settingsDataModule, settingsRepositoryModule, settingsDomainModule, settingsViewModelModule
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
    fun saveState(){
        sharedPref.edit()
            .putBoolean(KEY,darkTheme)
            .apply()
    }
    companion object{
        const val PREFERENCES = "pref"
        const val KEY = "key_theme"
    }
}