package org.example.project.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * Миграция с версии 1 на версию 2
 * Изменение типа ID с INTEGER (Long) на TEXT (String) для:
 * - groups.id
 * - users.id
 * - tasks.id
 * - tasks.groupId
 * - tasks.assigneeId
 * - notes.groupId
 * - comments.taskId
 * - group_user_cross_ref.groupId и userId
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        // Поскольку SQLite не поддерживает прямое изменение типа колонки,
        // нужно пересоздать таблицы с новой схемой

        // 1. Создаем новые таблицы с правильной схемой

        // Groups table
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS groups_new (
                id TEXT PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                description TEXT NOT NULL
            )
        """.trimIndent())

        // Users table
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS users_new (
                id TEXT PRIMARY KEY NOT NULL,
                email TEXT NOT NULL,
                privileges TEXT NOT NULL
            )
        """.trimIndent())

        // Tasks table
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS tasks_new (
                id TEXT PRIMARY KEY NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                groupId TEXT,
                assigneeId TEXT,
                noteId INTEGER,
                dueDate INTEGER NOT NULL,
                geotag TEXT NOT NULL,
                priority TEXT NOT NULL,
                status TEXT NOT NULL,
                FOREIGN KEY (groupId) REFERENCES groups_new(id) ON DELETE SET NULL,
                FOREIGN KEY (assigneeId) REFERENCES users_new(id) ON DELETE SET NULL,
                FOREIGN KEY (noteId) REFERENCES notes(id) ON DELETE CASCADE
            )
        """.trimIndent())

        connection.execSQL("""
            CREATE INDEX IF NOT EXISTS index_tasks_new_groupId ON tasks_new(groupId)
        """.trimIndent())

        connection.execSQL("""
            CREATE INDEX IF NOT EXISTS index_tasks_new_assigneeId ON tasks_new(assigneeId)
        """.trimIndent())

        connection.execSQL("""
            CREATE INDEX IF NOT EXISTS index_tasks_new_noteId ON tasks_new(noteId)
        """.trimIndent())

        // Notes table (только groupId меняется на TEXT)
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS notes_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                geotag TEXT NOT NULL,
                groupId TEXT,
                colorArgb INTEGER NOT NULL,
                creationDate INTEGER NOT NULL,
                contentMaxLines INTEGER NOT NULL,
                FOREIGN KEY (groupId) REFERENCES groups_new(id) ON DELETE SET NULL
            )
        """.trimIndent())

        connection.execSQL("""
            CREATE INDEX IF NOT EXISTS index_notes_new_groupId ON notes_new(groupId)
        """.trimIndent())

        // Comments table
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS comments_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                noteId INTEGER,
                taskId TEXT,
                author TEXT NOT NULL,
                content TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                FOREIGN KEY (noteId) REFERENCES notes_new(id) ON DELETE CASCADE,
                FOREIGN KEY (taskId) REFERENCES tasks_new(id) ON DELETE CASCADE
            )
        """.trimIndent())

        connection.execSQL("""
            CREATE INDEX IF NOT EXISTS index_comments_new_noteId ON comments_new(noteId)
        """.trimIndent())

        connection.execSQL("""
            CREATE INDEX IF NOT EXISTS index_comments_new_taskId ON comments_new(taskId)
        """.trimIndent())

        // Group-User cross reference table
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS group_user_cross_ref_new (
                groupId TEXT NOT NULL,
                userId TEXT NOT NULL,
                PRIMARY KEY (groupId, userId),
                FOREIGN KEY (groupId) REFERENCES groups_new(id) ON DELETE CASCADE,
                FOREIGN KEY (userId) REFERENCES users_new(id) ON DELETE CASCADE
            )
        """.trimIndent())

        connection.execSQL("""
            CREATE INDEX IF NOT EXISTS index_group_user_cross_ref_new_groupId ON group_user_cross_ref_new(groupId)
        """.trimIndent())

        connection.execSQL("""
            CREATE INDEX IF NOT EXISTS index_group_user_cross_ref_new_userId ON group_user_cross_ref_new(userId)
        """.trimIndent())

        // 2. Данные НЕ копируются, так как изменение типа с INTEGER на TEXT
        //    несовместимо с существующими данными (старые ID не будут валидными UUID)
        //    Пользователю нужно будет создать данные заново

        // 3. Удаляем старые таблицы
        connection.execSQL("DROP TABLE IF EXISTS comments")
        connection.execSQL("DROP TABLE IF EXISTS group_user_cross_ref")
        connection.execSQL("DROP TABLE IF EXISTS tasks")
        connection.execSQL("DROP TABLE IF EXISTS notes")
        connection.execSQL("DROP TABLE IF EXISTS groups")
        connection.execSQL("DROP TABLE IF EXISTS users")

        // 4. Переименовываем новые таблицы
        connection.execSQL("ALTER TABLE groups_new RENAME TO groups")
        connection.execSQL("ALTER TABLE users_new RENAME TO users")
        connection.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
        connection.execSQL("ALTER TABLE notes_new RENAME TO notes")
        connection.execSQL("ALTER TABLE comments_new RENAME TO comments")
        connection.execSQL("ALTER TABLE group_user_cross_ref_new RENAME TO group_user_cross_ref")
    }
}

/**
 * Миграция с версии 2 на версию 3
 * Добавление новых колонок:
 * - groups: ownerId, memberCount, createdAt, taskCount
 * - users: username
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        // Добавляем новые колонки в таблицу groups
        connection.execSQL("""
            ALTER TABLE groups ADD COLUMN ownerId TEXT NOT NULL DEFAULT ''
        """.trimIndent())

        connection.execSQL("""
            ALTER TABLE groups ADD COLUMN memberCount INTEGER NOT NULL DEFAULT 0
        """.trimIndent())

        connection.execSQL("""
            ALTER TABLE groups ADD COLUMN createdAt TEXT NOT NULL DEFAULT ''
        """.trimIndent())

        connection.execSQL("""
            ALTER TABLE groups ADD COLUMN taskCount INTEGER NOT NULL DEFAULT 0
        """.trimIndent())

        // Добавляем новую колонку в таблицу users
        connection.execSQL("""
            ALTER TABLE users ADD COLUMN username TEXT NOT NULL DEFAULT ''
        """.trimIndent())
    }
}

