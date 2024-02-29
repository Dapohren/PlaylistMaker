package com.example.playlistmaker.search.data.dto

import com.example.playlistmaker.search.domain.models.DataSongs

class TrackResponse(val results: List<DataSongs>) : Response()