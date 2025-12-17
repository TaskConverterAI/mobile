package org.example.project.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import org.example.project.data.database.converters.Converters
import org.example.project.data.database.dao.GroupDao
import org.example.project.data.database.dao.NoteDao
import org.example.project.data.database.dao.TaskDao
import org.example.project.data.database.dao.UserDao
import org.example.project.data.database.entities.CommentEntity
import org.example.project.data.database.entities.GroupEntity
import org.example.project.data.database.entities.GroupUserCrossRef
import org.example.project.data.database.entities.NoteEntity
import org.example.project.data.database.entities.TaskEntity
import org.example.project.data.database.entities.UserEntity

@Database(
    entities = [
        NoteEntity::class,
        TaskEntity::class,
        CommentEntity::class,
        GroupEntity::class,
        UserEntity::class,
        GroupUserCrossRef::class
    ],
    version = 5,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun groupDao(): GroupDao
    abstract fun userDao(): UserDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
