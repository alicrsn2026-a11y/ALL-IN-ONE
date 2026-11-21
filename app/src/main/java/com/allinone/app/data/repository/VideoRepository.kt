package com.allinone.app.data.repository

import com.allinone.app.data.db.dao.VideoDao
import com.allinone.app.data.db.entity.VideoEntity
import kotlinx.coroutines.flow.Flow

class VideoRepository(private val dao: VideoDao) {

    fun observeAll(): Flow<List<VideoEntity>> = dao.observeAll()

    fun observePlaylist(playlist: String): Flow<List<VideoEntity>> = dao.observePlaylist(playlist)

    suspend fun insert(video: VideoEntity): Long = dao.insert(video)

    suspend fun delete(video: VideoEntity) = dao.delete(video)
}
