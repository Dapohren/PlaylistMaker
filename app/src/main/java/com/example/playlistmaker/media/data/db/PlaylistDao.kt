package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.search.domain.models.DataSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlist_table WHERE playlist_id = :id")
    fun deletePlaylist(id: Int?)

    @Query("SELECT * FROM playlist_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_table WHERE playlist_id = :id")
    fun getPlaylistEntity(id: Int?): PlaylistEntity

    @Query("UPDATE playlist_table SET playlist_id =:id, playlistName =:name, playlistDescription =:description, playlistImage =:image WHERE playlist_id = :id")
    fun editPlaylist(id: Int?, name: String, description: String, image: String?)

    @Query("UPDATE playlist_table SET addedTracksId = :newTracksId, addedTracksNumber = :number WHERE playlist_id = :id")
    fun changeTracksList(newTracksId: String, id: Int, number: Int)



}