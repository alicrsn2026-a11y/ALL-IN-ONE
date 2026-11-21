package com.allinone.app.data.repository

import com.allinone.app.data.db.dao.ResearchDao
import com.allinone.app.data.db.entity.ResearchResultEntity
import kotlinx.coroutines.flow.Flow

class ResearchRepository(private val dao: ResearchDao) {

    fun observeAll(): Flow<List<ResearchResultEntity>> = dao.observeAll()

    fun search(q: String): Flow<List<ResearchResultEntity>> = dao.search(q)

    suspend fun insert(result: ResearchResultEntity): Long = dao.insert(result)

    suspend fun delete(result: ResearchResultEntity) = dao.delete(result)
}
