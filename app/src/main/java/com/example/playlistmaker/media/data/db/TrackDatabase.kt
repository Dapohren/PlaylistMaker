package com.example.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(version = 2, entities = [TrackEntity::class, PlaylistEntity::class, TracksForPlaylistEntity::class])
abstract class TrackDatabase: RoomDatabase() {
    abstract fun trackDao() : TrackDao

    abstract fun playlistDao() :PlaylistDao

    abstract fun tracksForPlaylistDao() : TracksForPlaylistDao
}