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
fun Task.toEntity(
    id: String = "",
    groupId: String? = null,
    assigneeId: String? = null,
    noteId: Long? = null
): TaskEntity {
    return TaskEntity(
        id = id.ifEmpty { Uuid.random().toString() },
        title = title,
        description = description,
        groupId = groupId,  // Используем переданный groupId
        assigneeId = assigneeId,  // Используем переданный assigneeId
        noteId = noteId,  // Используем переданный noteId
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
        group = group?.toGroup() ?: Group(
            id = "",
            name = "Без группы",
            description = "",
            ownerId = "",
            memberCount = 0,
            members = mutableListOf(),
            createdAt = "",
            taskCount = 0
        ),
        assignee = assignee?.toUser() ?: User(
            id = "",
            email = "Не назначен",
            username = "Не назначен",
            privileges = org.example.project.data.commonData.Privileges.member
        ),
        dueDate = task.dueDate,
        geotag = task.geotag,
        priority = task.priority,
        status = task.status
    )
}

// Convert Comment to CommentEntity for Task
fun Comment.toTaskCommentEntity(taskId: String): CommentEntity {
    return CommentEntity(
        id = id,
        taskId = taskId,
        author = author,
        content = content,
        timestamp = timestamp
    )
}

