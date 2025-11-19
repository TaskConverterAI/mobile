package org.example.project.data.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Data class для получения группы вместе со всеми пользователями
 */
data class GroupWithUsers(
    @Embedded val group: GroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = GroupUserCrossRef::class,
            parentColumn = "groupId",
            entityColumn = "userId"
        )
    )
    val users: List<UserEntity>
)

