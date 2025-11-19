package org.example.project.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity (
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val ownerId: String,
    val memberCount: Int,
    val createdAt: String,
    val taskCount: Int = 0
)
