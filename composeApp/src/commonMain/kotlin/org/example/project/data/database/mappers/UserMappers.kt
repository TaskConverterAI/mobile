package org.example.project.data.database.mappers

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
fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        email = email,
        privileges = privileges
    )
}

