package org.example.project.data.database.mappers

import org.example.project.data.commonData.Group
import org.example.project.data.database.entities.GroupEntity
import org.example.project.data.database.entities.GroupWithUsers

// Convert GroupEntity to Group (без пользователей)
fun GroupEntity.toGroup(): Group {
    return Group(
        id = id,
        name = name,
        description = description,
        users = emptyList()  // Пользователи не загружены
    )
}

// Convert GroupWithUsers to Group (с пользователями)
fun GroupWithUsers.toGroup(): Group {
    return Group(
        id = group.id,
        name = group.name,
        description = group.description,
        users = users.map { it.toUser() }
    )
}

// Convert Group to GroupEntity (только для вставки/обновления)
fun Group.toEntity(): GroupEntity {
    return GroupEntity(
        id = id,
        name = name,
        description = description
    )
}

