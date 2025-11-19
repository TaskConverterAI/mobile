package org.example.project.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import org.example.project.data.database.entities.CommentEntity
import org.example.project.data.database.entities.TaskEntity

data class TaskWithComments(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val comments: List<CommentEntity>
)

