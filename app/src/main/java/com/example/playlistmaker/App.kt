package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    private lateinit var sharedPref: SharedPreferences
    var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPref.getBoolean(KEY, false)
        switchTheme(darkTheme)


    }
    fun switchTheme(darkThemeEnabled: Boolean) {
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