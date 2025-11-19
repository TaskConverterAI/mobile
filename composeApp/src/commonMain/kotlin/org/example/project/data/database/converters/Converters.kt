package org.example.project.data.database.converters

import androidx.room.TypeConverter
import org.example.project.data.commonData.Priority
import org.example.project.data.commonData.Privileges
import org.example.project.data.commonData.Status

class Converters {

    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(value: String): Priority {
        return Priority.valueOf(value)
    }

    @TypeConverter
    fun fromStatus(status: Status): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): Status {
        return Status.valueOf(value)
    }

    @TypeConverter
    fun fromPrivileges(privileges: Privileges): String {
        return privileges.name
    }

    @TypeConverter
    fun toPrivileges(value: String): Privileges {
        return Privileges.valueOf(value)
    }
}
