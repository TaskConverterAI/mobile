package org.example.project.data.commonData

enum class Privileges {
    member,
    owner,
    admin
}

data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val privileges: Privileges
)
