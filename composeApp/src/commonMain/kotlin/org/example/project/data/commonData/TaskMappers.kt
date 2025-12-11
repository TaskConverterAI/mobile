package org.example.project.data.commonData

import androidx.compose.material3.PrimaryTabRow
import org.example.project.data.database.entities.TaskEntity
import org.example.project.data.network.models.CreateTaskRequest
import org.example.project.data.network.models.TaskDetailsDto
import org.example.project.data.network.models.TaskDto
import org.example.project.data.network.models.UpdateTaskRequest
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun Task.toTaskDto(): TaskDto {
    return TaskDto(
        id = id,
        title = title,
        description = description,
        authorId = authorId,
        priority = priority.name,
        groupId = groupId,
        status = status.toString(),
        doerId = assignee,
        createdAt = Instant.fromEpochMilliseconds(createAt).toString(),
        deadline = dueDate?.toDeadlineDto(),
        location =  geotag?.toLocationDto()
    )
}

@OptIn(ExperimentalTime::class)
fun TaskDto.toTask(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        authorId = authorId,
        priority = Priority.valueOf(priority),
        groupId = groupId,
        status = Status.valueOf(status),
        assignee = doerId,
        createAt = Instant.parse(createdAt).toEpochMilliseconds(),
        comments = emptyList(),
        dueDate = deadline?.toDeadline(),
        geotag = location?.toLocation()

    )
}

@OptIn(ExperimentalTime::class)
fun Task.toTaskDetailsDto(): TaskDetailsDto {
    return TaskDetailsDto(
        id = id,
        title = title,
        description = description,
        authorId = authorId,
        priority = priority.name,
        groupId = groupId,
        status = status.toString(),
        doerId = assignee,
        createdAt = Instant.fromEpochMilliseconds(createAt).toString(),
        deadline = dueDate?.toDeadlineDto(),
        location =  geotag?.toLocationDto(),
        comments = comments.map { comment -> comment.toCommentDto() }
    )
}

@OptIn(ExperimentalTime::class)
fun TaskDetailsDto.toTask(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        authorId = authorId,
        priority = Priority.valueOf(priority),
        groupId = groupId,
        status = Status.valueOf(status),
        assignee = doerId,
        createAt = Instant.parse(createdAt).toEpochMilliseconds(),
        comments = comments.map { comment -> comment.toComment()},
        dueDate = deadline?.toDeadline(),
        geotag = location?.toLocation()

    )
}

@OptIn(ExperimentalTime::class)
fun Task.toCreateRequest() : CreateTaskRequest {
    return CreateTaskRequest(
        title = title,
        description = description,
        authorId = authorId,
        priority = priority.name,
        groupId = groupId,
        doerId = assignee,
        deadline = dueDate?.toDeadlineDto(),
        location = geotag?.toLocationDto()
    )
}

@OptIn(ExperimentalTime::class)
fun Task.toUpdateRequest() : UpdateTaskRequest {
    return UpdateTaskRequest(
        id = id,
        title = title,
        description = description,
        priority = priority.name,
        doerId = assignee,
        deadline = dueDate?.toDeadlineDto(),
        location = geotag?.toLocationDto(),
        status = status.name
    )
}