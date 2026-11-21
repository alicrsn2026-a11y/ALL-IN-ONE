package com.allinone.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.allinone.app.data.db.dao.ChatDao
import com.allinone.app.data.db.dao.EmailDao
import com.allinone.app.data.db.dao.NoteDao
import com.allinone.app.data.db.dao.ResearchDao
import com.allinone.app.data.db.dao.ScanDao
import com.allinone.app.data.db.dao.SpaceDao
import com.allinone.app.data.db.dao.TaskDao
import com.allinone.app.data.db.dao.VideoDao
import com.allinone.app.data.db.entity.ConversationEntity
import com.allinone.app.data.db.entity.EmailEntity
import com.allinone.app.data.db.entity.MessageEntity
import com.allinone.app.data.db.entity.NoteEntity
import com.allinone.app.data.db.entity.ResearchResultEntity
import com.allinone.app.data.db.entity.ScanEntity
import com.allinone.app.data.db.entity.SpaceItemEntity
import com.allinone.app.data.db.entity.TaskEntity
import com.allinone.app.data.db.entity.VideoEntity

@Database(
    entities = [
        SpaceItemEntity::class,
        NoteEntity::class,
        TaskEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        ResearchResultEntity::class,
        EmailEntity::class,
        ScanEntity::class,
        VideoEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AllInOneDatabase : RoomDatabase() {
    abstract fun spaceDao(): SpaceDao
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun chatDao(): ChatDao
    abstract fun researchDao(): ResearchDao
    abstract fun emailDao(): EmailDao
    abstract fun scanDao(): ScanDao
    abstract fun videoDao(): VideoDao
}
