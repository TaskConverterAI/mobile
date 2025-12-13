import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import org.example.project.data.commonData.Comment
import org.example.project.data.commonData.Note
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CommentDialog(
    comments: MutableList<Comment>,
    onSave: (Comment) -> Unit = {},
    userId: Long,
    note: Note
) {
    var showDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Button(
        onClick = {
            showDialog = true
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        border = null
    ) {
        Text("+ добавить комментарий")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Добавьте комментарий") },
            text = {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        placeholder = { Text("Введите текст") },
                        singleLine = true,
                        shape = MaterialTheme.shapes.small
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newComment = Comment(
                            taskId = note.id,
                            author = userId,
                            content = text,
                            timestamp = Clock.System.now().toEpochMilliseconds()
                        )
                        onSave(newComment)
                        comments.add(newComment)
                        showDialog = false
                    },
                    enabled = text.isNotBlank()
                ) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        text = ""
                        showDialog = false
                    }
                ) {
                    Text("Отмена")
                }
            }
        )

//        LaunchedEffect(showDialog) {
//            if (showDialog) {
//                delay(100) // Небольшая задержка для анимации
//                focusRequester.requestFocus()
//            }
//        }
    }
}