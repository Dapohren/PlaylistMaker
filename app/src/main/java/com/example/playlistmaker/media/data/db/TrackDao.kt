package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTrackToFav(song: TrackEntity)
    @Delete(entity = TrackEntity::class)
    fun deleteTrackFromFav(song: TrackEntity)
    @Query("SELECT * FROM track_table")
    fun getAllTracksFromFav() : Flow<List<TrackEntity>>
    @Query("SELECT id FROM track_table")
    suspend fun getAllIdTracks() : List<Long>
}