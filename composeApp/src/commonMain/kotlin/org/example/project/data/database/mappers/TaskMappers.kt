package org.example.project.data.database.mappers

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.example.project.data.commonData.Comment
import org.example.project.data.commonData.Group
import org.example.project.data.commonData.Task
import org.example.project.data.commonData.User
import org.example.project.data.database.entities.CommentEntity
import org.example.project.data.database.entities.TaskEntity
import org.example.project.data.database.entities.TaskWithDetails

// Convert Task to TaskEntity (только для вставки/обновления)
@OptIn(ExperimentalUuidApi::class)
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        groupId = null,
        assigneeId = null,
        noteId = null,
        dueDate = dueDate,
        geotag = geotag,
        priority = priority,
        status = status
    )
}

// Convert TaskWithDetails to Task (полное преобразование с группой, исполнителем и заметкой)
fun TaskWithDetails.toTask(comments: List<Comment> = emptyList()): Task {
    return Task(
        id = task.id,
        title = task.title,
        description = task.description,
        comments = comments,
        group = null,
        assignee = null,
        dueDate = task.dueDate,
        geotag = task.geotag,
        priority = task.priority,
        status = task.status
    )
}

// Convert Comment to CommentEntity for Task
fun Comment.toTaskCommentEntity(taskId: Long): CommentEntity {
    return CommentEntity(
        id = id,
        taskId = taskId,
        author = author,
        content = content,
        timestamp = timestamp
    )
}
