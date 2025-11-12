package org.example.project.data.commonData

enum class Privileges {
    FULL,
    PART
}

data class User(
    val email: String,
    val privileges: Privileges
)
