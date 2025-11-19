package org.example.project.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("noteId"), Index("taskId")]
)
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noteId: Long? = null,
    val taskId: String? = null,
    val author: String,
    val content: String,
    val timestamp: Long
)

