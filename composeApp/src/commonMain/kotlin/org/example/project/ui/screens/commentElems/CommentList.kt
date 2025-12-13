package org.example.project.ui.screens.commentElems

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.data.commonData.Comment
import org.example.project.data.commonData.Group
import org.example.project.data.commonData.Note
import org.example.project.data.commonData.User
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// Модель данных для элемента списка
data class ListItem(
    val comment: Comment,
    val title: String,
    val subtitle: String
)

@Composable
fun ListItemWithButton(
    item: ListItem,
    modifier: Modifier = Modifier,
    onButtonClick: (Long) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Левая часть: две надписи
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Правая часть: кнопка
        Spacer(modifier = Modifier.width(16.dp))

        IconButton(onClick = { onButtonClick(item.comment.id) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Удалить",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CommentList(
    comments: List<Comment>,
    userId: Long,
    group: Group?,
    onDelete: (Long) -> Unit = {}
) {
    // Создаем State для отслеживания изменений списка комментариев
    val commentsList by remember(comments) {
        mutableStateOf(comments)
    }

    val userById = remember(group) {
        group?.members?.associateBy { it.id } ?: emptyMap()
    }

    val items by remember {
        derivedStateOf {
            commentsList.map { comment ->
                val authorName = when {
                    comment.author == userId -> "меня"
                    else -> userById[comment.author]?.username ?: "Неизвестный"
                }

                val formattedTime = try {
                    Instant
                        .fromEpochMilliseconds(comment.timestamp)
                        .toString()
                        .split(".")[0]
                        .replace("T", " ")
                } catch (e: Exception) {
                    "Некорректная дата"
                }

                ListItem(
                    comment = comment,
                    title = "От $authorName - $formattedTime",
                    subtitle = comment.content
                )
            }
        }
    }

    Row {
    // ✅ Ключевое исправление: оборачиваем в Box с ограниченной высотой
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Комментариев пока нет",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // ✅ Важно: LazyColumn должен быть в Box с fillMaxSize
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    items = items,
                    key = { it.comment.id }
                ) { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        ListItemWithButton(
                            item = item,
                            onButtonClick = { commentId ->
                                onDelete(commentId)
                            }
                        )
                    }

                    if (item != items.last()) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
        }
}