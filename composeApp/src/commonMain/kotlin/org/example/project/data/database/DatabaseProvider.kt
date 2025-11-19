package org.example.project.data.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.example.project.data.database.repository.NoteRepository
import org.example.project.data.database.repository.TaskRepository

object DatabaseProvider {

    private var database: AppDatabase? = null

    fun getDatabase(): AppDatabase {
        return database ?: getDatabaseBuilder()
            .setDriver(androidx.sqlite.driver.bundled.BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            // Раскомментируйте следующую строку для разработки, если нужно удалить старую БД при ошибке миграции
            // .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
            .also { database = it }
    }

    fun getNoteRepository(): NoteRepository {
        return NoteRepository(getDatabase())
    }

    fun getTaskRepository(): TaskRepository {
        return TaskRepository(getDatabase())
    }

    // For testing or manual reset
    fun closeDatabase() {
        database?.close()
        database = null
    }
}

