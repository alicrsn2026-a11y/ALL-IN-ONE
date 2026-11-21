package com.allinone.app.data

import android.content.Context
import androidx.room.Room
import com.allinone.app.data.db.AllInOneDatabase
import com.allinone.app.data.repository.ChatRepository
import com.allinone.app.data.repository.EmailRepository
import com.allinone.app.data.repository.NoteRepository
import com.allinone.app.data.repository.ResearchRepository
import com.allinone.app.data.repository.ScanRepository
import com.allinone.app.data.repository.SettingsRepository
import com.allinone.app.data.repository.SpaceRepository
import com.allinone.app.data.repository.TaskRepository
import com.allinone.app.data.repository.VideoRepository
import com.allinone.app.data.security.EncryptedPrefs

/**
 * Simple manual dependency-injection container. Kept intentionally lightweight
 * (no Hilt/Koin) to reduce build complexity while keeping a single place to
 * wire dependencies.
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val database: AllInOneDatabase by lazy {
        Room.databaseBuilder(
            appContext,
            AllInOneDatabase::class.java,
            "all_in_one.db"
        ).build()
    }

    val encryptedPrefs: EncryptedPrefs by lazy {
        EncryptedPrefs.create(appContext)
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(appContext)
    }

    val spaceRepository: SpaceRepository by lazy { SpaceRepository(database.spaceDao()) }
    val noteRepository: NoteRepository by lazy { NoteRepository(database.noteDao()) }
    val taskRepository: TaskRepository by lazy { TaskRepository(database.taskDao()) }
    val chatRepository: ChatRepository by lazy { ChatRepository(database.chatDao()) }
    val researchRepository: ResearchRepository by lazy { ResearchRepository(database.researchDao()) }
    val emailRepository: EmailRepository by lazy { EmailRepository(database.emailDao()) }
    val scanRepository: ScanRepository by lazy { ScanRepository(database.scanDao()) }
    val videoRepository: VideoRepository by lazy { VideoRepository(database.videoDao()) }
}
