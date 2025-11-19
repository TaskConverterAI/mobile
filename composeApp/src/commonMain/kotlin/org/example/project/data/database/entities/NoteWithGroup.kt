package org.example.project.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class для получения заметки только с группой (без задач)
 */
data class NoteWithGroup(
    @Embedded val note: NoteEntity,

    @Relation(
        parentColumn = "groupId",
        entityColumn = "id"
    )
    val group: GroupEntity?
)

