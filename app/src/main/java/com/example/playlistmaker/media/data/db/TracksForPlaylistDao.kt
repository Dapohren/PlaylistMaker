package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TracksForPlaylistDao {
    @Insert(entity = TracksForPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TracksForPlaylistEntity)

    @Delete(entity = TracksForPlaylistEntity::class)
    fun deleteTrackEntity(track: TracksForPlaylistEntity)

    @Query("SELECT * FROM track_playlist_table")
    fun getTracksInPlaylists(): List<TracksForPlaylistEntity>
}