package org.example.project.data.database.mappers

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.example.project.data.commonData.User
import org.example.project.data.database.entities.UserEntity

// Convert UserEntity to User
fun UserEntity.toUser(): User {
    return User(
        id = id,
        email = email,
        privileges = privileges
    )
}

// Convert User to UserEntity (только для вставки/обновления)
@OptIn(ExperimentalUuidApi::class)
fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id.ifEmpty { Uuid.random().toString() },
        email = email,
        privileges = privileges
    )
}

