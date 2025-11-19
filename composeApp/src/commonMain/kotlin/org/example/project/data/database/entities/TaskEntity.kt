package org.example.project.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.example.project.data.commonData.Priority
import org.example.project.data.commonData.Status

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["assigneeId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["groupId"]),
        Index(value = ["assigneeId"]),
        Index(value = ["noteId"])
    ]
)
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val groupId: String? = null,  // Ссылка на группу (может быть null)
    val assigneeId: String? = null,  // Ссылка на исполнителя (может быть null)
    val noteId: Long? = null,  // Ссылка на заметку (может быть null)
    val dueDate: Long,
    val geotag: String,
    val priority: Priority,
    val status: Status
)
