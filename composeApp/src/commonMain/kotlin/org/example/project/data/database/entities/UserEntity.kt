package org.example.project.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.example.project.data.commonData.Privileges

@Entity(tableName = "users")
data class UserEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val username: String,
    val privileges: Privileges
)
