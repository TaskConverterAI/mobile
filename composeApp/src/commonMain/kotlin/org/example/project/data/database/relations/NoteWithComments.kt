package org.example.project.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import org.example.project.data.database.entities.CommentEntity
import org.example.project.data.database.entities.NoteEntity

data class NoteWithComments(
    @Embedded val note: NoteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val comments: List<CommentEntity>
)
