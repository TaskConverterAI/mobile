package org.example.project.data.database.mappers

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.example.project.data.commonData.Group
import org.example.project.data.database.entities.GroupEntity
import org.example.project.data.database.entities.GroupWithUsers

// Convert GroupEntity to Group (без пользователей)
fun GroupEntity.toGroup(): Group {
    return Group(
        id = id,
        name = name,
        description = description,
        ownerId = ownerId,
        memberCount = memberCount,
        members = mutableListOf(),  // Пользователи не загружены
        createdAt = createdAt,
        taskCount = taskCount
    )
}

// Convert GroupWithUsers to Group (с пользователями)
fun GroupWithUsers.toGroup(): Group {
    return Group(
        id = group.id,
        name = group.name,
        description = group.description,
        ownerId = group.ownerId,
        memberCount = group.memberCount,
        members = users.map { it.toUser() }.toMutableList(),
        createdAt = group.createdAt,
        taskCount = group.taskCount
    )
}

// Convert Group to GroupEntity (только для вставки/обновления)
@OptIn(ExperimentalUuidApi::class)
fun Group.toEntity(): GroupEntity {
    return GroupEntity(
        id = id.ifEmpty { Uuid.random().toString() },
        name = name,
        description = description,
        ownerId = ownerId,
        memberCount = memberCount,
        createdAt = createdAt,
        taskCount = taskCount
    )
}

