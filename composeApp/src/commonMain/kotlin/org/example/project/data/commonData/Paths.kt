package org.example.project.data.commonData

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

import org.jetbrains.compose.resources.vectorResource
import taskconvertaiapp.composeapp.generated.resources.Res
import taskconvertaiapp.composeapp.generated.resources.*

enum class Destination(
    val route: String,
    val label: String,
    val commonIcon: @Composable () -> ImageVector,
    val selectedIcon : @Composable () -> ImageVector,
    val contentDescription: String? = null
) {
    NOTES("notes", "Заметки",
        {vectorResource(Res.drawable.clipboard_list_outlined)},
        {vectorResource(Res.drawable.clipboard_list_filled)}
    ),
    TASKS("tasks", "Задачи",
        {vectorResource(Res.drawable.calendar_outlined)},
        {vectorResource(Res.drawable.calendar_filled)}
    ),
    GROUPS("groups", "Группы",
        {vectorResource(Res.drawable.user_group_outlined)},
        {vectorResource(Res.drawable.user_group_filled)}
    ),
    SETTINGS("settings", "Опции",
        {vectorResource(Res.drawable.cog_outlined)},
        {vectorResource(Res.drawable.cog_filled)}
    )
}
