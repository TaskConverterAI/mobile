package org.example.project.data.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Data class для получения пользователя вместе со всеми его группами
 */
data class UserWithGroups(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = GroupUserCrossRef::class,
            parentColumn = "userId",
            entityColumn = "groupId"
        )
    )
    val groups: List<GroupEntity>
)

