package org.example.project.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class для получения задачи вместе с группой, исполнителем и заметкой
 */
data class TaskWithDetails(
    @Embedded val task: TaskEntity,

    @Relation(
        parentColumn = "groupId",
        entityColumn = "id"
    )
    val group: GroupEntity?,

    @Relation(
        parentColumn = "assigneeId",
        entityColumn = "id"
    )
    val assignee: UserEntity?,

    @Relation(
        parentColumn = "noteId",
        entityColumn = "id"
    )
    val note: NoteEntity?
)

