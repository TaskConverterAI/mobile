package org.example.project.ui.screens.groupsScreen.creatingGroupScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.example.project.ui.theme.LightGray
import org.example.project.ui.theme.TaskConvertAIAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    navController: NavController,
    viewModel: CreateGroupViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Назад", color = MaterialTheme.colorScheme.primary) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                HorizontalDivider(thickness = 1.dp, color = LightGray)
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Spacer(Modifier.height(12.dp))

                Text(
                    "Создание группы",
                    style = MaterialTheme.typography.displayLarge
                )

                Spacer(Modifier.height(24.dp))

                // TITLE
                Text("Название", style = MaterialTheme.typography.bodyLarge)
                OutlinedTextField(
                    value = state.groupName,
                    onValueChange = viewModel::onGroupNameChange,
                    placeholder = { Text(
                        text = "Work Group",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    ) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(20.dp))

                // DESCRIPTION
                Text("Описание", style = MaterialTheme.typography.bodyLarge)
                OutlinedTextField(
                    value = state.description,
                    onValueChange = viewModel::onDescriptionChange,
                    placeholder = { Text(
                        text = "пара слов о группе",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    ) },
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    minLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.error,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(20.dp))

                // PARTICIPANTS
                Text("Участники", style = MaterialTheme.typography.bodyLarge)

                Spacer(Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .border(1.dp, LightGray, RoundedCornerShape(12.dp))
                ) {

                    state.participants.forEachIndexed { index, email ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(email, modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.removeParticipant(index) }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "remove",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        HorizontalDivider(color = LightGray.copy(alpha = 0.5f))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                            .clickable { viewModel.openAddDialog() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "+ добавить участника",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.createGroup {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Создать")
            }
        }
    }

    // DIALOG
    if (state.addDialogVisible) {
        AlertDialog(
            onDismissRequest = viewModel::closeAddDialog,
            confirmButton = {},
            title = { Text("Добавить участника") },
            text = {
                Column {

                    OutlinedTextField(
                        value = state.newEmail,
                        onValueChange = viewModel::onNewEmailChange,
                        placeholder = { Text("user@example.com") },
                        label = { Text("Email") },
                        singleLine = true,
                        isError = state.emailError != null
                    )

                    state.emailError?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = viewModel::addParticipant) {
                            Icon(Icons.Default.Check, contentDescription = "add")
                        }

                        IconButton(onClick = viewModel::closeAddDialog) {
                            Icon(Icons.Default.Close, contentDescription = "cancel")
                        }
                    }
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun CreateGroupScreenPreview() {
    val vm = CreateGroupViewModel()

    vm.onGroupNameChange("Work Group")
    vm.onDescriptionChange("пара слов о группе")
    vm.onNewEmailChange("test@gmail.com")
    vm.addParticipant()
    vm.onNewEmailChange("user2@gmail.com")
    vm.addParticipant()

    TaskConvertAIAppTheme {
        CreateGroupScreen(
            navController = rememberNavController(),
            viewModel = vm
        )
    }
}
