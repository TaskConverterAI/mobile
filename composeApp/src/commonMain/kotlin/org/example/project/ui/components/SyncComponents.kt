package org.example.project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.data.sync.SyncState

/**
 * Кнопка синхронизации с индикацией состояния
 */
@Composable
fun SyncButton(
    syncState: SyncState,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    when (syncState) {
        is SyncState.Idle -> {
            FilledTonalButton(
                onClick = onSyncClick,
                enabled = enabled,
                modifier = modifier
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Синхронизировать",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Синхронизировать")
            }
        }

        is SyncState.Syncing -> {
            FilledTonalButton(
                onClick = {},
                enabled = false,
                modifier = modifier
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.width(8.dp))
                Text("Синхронизация...")
            }
        }

        is SyncState.Success -> {
            FilledTonalButton(
                onClick = onSyncClick,
                enabled = enabled,
                modifier = modifier,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = "Синхронизировано",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text("Синхронизировано (${syncState.syncedCount})")
            }
        }

        is SyncState.Error -> {
            FilledTonalButton(
                onClick = onSyncClick,
                enabled = enabled,
                modifier = modifier,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Ошибка",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(8.dp))
                Text("Повторить")
            }
        }
    }
}

/**
 * Индикатор состояния синхронизации в виде иконки
 */
@Composable
fun SyncStatusIcon(
    syncState: SyncState,
    modifier: Modifier = Modifier
) {
    when (syncState) {
        is SyncState.Idle -> {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = "Не синхронизировано",
                modifier = modifier,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        is SyncState.Syncing -> {
            CircularProgressIndicator(
                modifier = modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        is SyncState.Success -> {
            Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = "Синхронизировано",
                modifier = modifier,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        is SyncState.Error -> {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Ошибка синхронизации",
                modifier = modifier,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * Карточка с информацией о синхронизации
 */
@Composable
fun SyncStatusCard(
    syncState: SyncState,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (syncState) {
                is SyncState.Success -> MaterialTheme.colorScheme.primaryContainer
                is SyncState.Error -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (syncState) {
                        is SyncState.Idle -> "Готово к синхронизации"
                        is SyncState.Syncing -> "Синхронизация..."
                        is SyncState.Success -> "Синхронизировано"
                        is SyncState.Error -> "Ошибка синхронизации"
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                if (syncState is SyncState.Success) {
                    Text(
                        text = "Синхронизировано заметок: ${syncState.syncedCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                if (syncState is SyncState.Error) {
                    Text(
                        text = syncState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            SyncButton(
                syncState = syncState,
                onSyncClick = onSyncClick
            )
        }
    }
}

/**
 * Snackbar с информацией о синхронизации
 */
@Composable
fun SyncSnackbar(
    syncState: SyncState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (syncState is SyncState.Success || syncState is SyncState.Error) {
        Snackbar(
            modifier = modifier,
            action = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            },
            containerColor = when (syncState) {
                is SyncState.Success -> MaterialTheme.colorScheme.primaryContainer
                is SyncState.Error -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SyncStatusIcon(syncState = syncState)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = when (syncState) {
                        is SyncState.Success -> "Синхронизировано: ${syncState.syncedCount} заметок"
                        is SyncState.Error -> "Ошибка: ${syncState.message}"
                        else -> ""
                    }
                )
            }
        }
    }
}

