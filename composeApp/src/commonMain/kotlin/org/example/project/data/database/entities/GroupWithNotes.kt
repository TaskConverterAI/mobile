package org.example.project.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class для получения группы вместе со всеми заметками
 */
data class GroupWithNotes(
    @Embedded val group: GroupEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "groupId"
    )
    val notes: List<NoteEntity>
)

