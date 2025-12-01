package org.example.project.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val ownerId: Long,
    val memberCount: Int,
    val createdAt: Long,
    val taskCount: Int = 0
)
