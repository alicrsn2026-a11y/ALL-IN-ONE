package com.allinone.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.allinone.app.data.db.entity.ResearchResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResearchDao {
    @Query("SELECT * FROM research_results ORDER BY savedAt DESC")
    fun observeAll(): Flow<List<ResearchResultEntity>>

    @Query("SELECT * FROM research_results WHERE LOWER(title) LIKE '%' || LOWER(:q) || '%' OR LOWER(query) LIKE '%' || LOWER(:q) || '%'")
    fun search(q: String): Flow<List<ResearchResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: ResearchResultEntity): Long

    @Delete
    suspend fun delete(result: ResearchResultEntity)
}
