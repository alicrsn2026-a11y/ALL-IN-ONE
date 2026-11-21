package com.allinone.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.allinone.app.data.db.entity.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE playlist = :playlist ORDER BY addedAt DESC")
    fun observePlaylist(playlist: String): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video: VideoEntity): Long

    @Delete
    suspend fun delete(video: VideoEntity)
}
