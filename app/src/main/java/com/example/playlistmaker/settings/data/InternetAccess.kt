package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.domain.model.EmailData

interface InternetAccess {
    fun shareLink(url: String, title: String)
    fun openLink(url: String)
    fun openEmail(data: EmailData)
}