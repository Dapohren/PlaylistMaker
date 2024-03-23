package com.example.playlistmaker.settings.domain.Impl

import com.example.playlistmaker.settings.domain.model.EmailData
import com.example.playlistmaker.settings.data.InternetAccess
import com.example.playlistmaker.settings.domain.SharingInteractor

class SharingInteractorImpl(private val internetAccess: InternetAccess) : SharingInteractor {
    override fun openSupport(email: String, subject: String, text: String) {
        internetAccess.openEmail(data = getSupportEmailData(
            email = email,
            subject = subject,
            text = text
        ))
    }

    override fun openTerms(link: String) {
        internetAccess.openLink(url = link)
    }

    override fun shareApp(link: String, title: String) {
        internetAccess.shareLink(url = link, title = title)
    }
    private fun getSupportEmailData(email: String, subject: String, text: String): EmailData {
        return EmailData(
            email = email,
            subject = subject,
            text = text
        )
    }
}