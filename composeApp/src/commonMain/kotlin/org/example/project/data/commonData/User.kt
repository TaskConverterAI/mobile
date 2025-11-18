package org.example.project.data.commonData

enum class Privileges {
    FULL,
    PART
}

data class User(
    val id: Long = 0,
    val email: String,
    val privileges: Privileges
)
