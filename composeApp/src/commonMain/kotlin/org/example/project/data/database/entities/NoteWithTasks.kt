package org.example.project.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class для получения заметки вместе с группой и всеми связанными задачами
 */
data class NoteWithTasks(
    @Embedded val note: NoteEntity,

    @Relation(
        parentColumn = "groupId",
        entityColumn = "id"
    )
    val group: GroupEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val tasks: List<TaskEntity>
)

