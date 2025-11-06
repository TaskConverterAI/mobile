package org.example.project.ui.screens.notesScreen.creatingNoteScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.time.ExperimentalTime

import org.example.project.ui.theme.LightGray

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CheckTranscribingScreen(navController: NavController) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Назад", color = MaterialTheme.colorScheme.primary) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Вернуться назад",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = LightGray
                )
            }
        }
    ) { paddingValues ->
        var explanation by remember { mutableStateOf("") }
        var text by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Проверь текст",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(20.dp).scale(1.1F)
                )

                Text(
                    "Можешь отредактировать распознанный текст, а также добавить пояснение. " +
                            "ИИ создаст заметку и задачи на основе этого",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    label = { Text("Пояснение") },
                    placeholder = { Text("Пара слов о теме, спикерах и том," +
                            "что важно извлечь из записи") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = LightGray
                    ),
                    trailingIcon = {
                        if (explanation.isNotEmpty()) {
                            IconButton(onClick = { explanation = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Очистить",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Текст") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = LightGray
                    ),
                    trailingIcon = {
                        if (text.isNotEmpty()) {
                            IconButton(onClick = { text = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Очистить",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
            //end of scrollable content

            Button(
                onClick = {
                    // ToDo: Start analysis process
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .height(56.dp),
                enabled = true
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Анализировать",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}
