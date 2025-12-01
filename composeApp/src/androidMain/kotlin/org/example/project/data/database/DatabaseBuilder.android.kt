package org.example.project.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.example.project.data.database.migrations.MIGRATION_1_2
import org.example.project.data.database.migrations.MIGRATION_2_3
import org.example.project.data.database.migrations.MIGRATION_3_4

private lateinit var appContext: Context

fun initDatabase(context: Context) {
    appContext = context.applicationContext
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = appContext.getDatabasePath("taskconvert_ai.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
//    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
    ).fallbackToDestructiveMigration(true)
}
