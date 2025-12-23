package org.example.project.data.commonData

enum class Privileges {
    member,
    admin,
    owner
}

data class User(
    val id: Long = 0,
    val email: String = "",
    val username: String = "",
    val privileges: Privileges
)
