package com.allinone.app.data.repository

import com.allinone.app.data.db.dao.TaskDao
import com.allinone.app.data.db.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {

    fun observeAll(): Flow<List<TaskEntity>> = dao.observeAll()

    fun observeOpen(): Flow<List<TaskEntity>> = dao.observeOpen()

    suspend fun insert(task: TaskEntity): Long = dao.insert(task)

    suspend fun update(task: TaskEntity) = dao.update(task)

    suspend fun delete(task: TaskEntity) = dao.delete(task)

    suspend fun toggle(task: TaskEntity) {
        dao.update(task.copy(done = !task.done))
    }
}
