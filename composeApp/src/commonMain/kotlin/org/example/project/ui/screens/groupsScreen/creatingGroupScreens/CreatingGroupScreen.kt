package org.example.project.ui.screens.groupsScreen.creatingGroupScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.example.project.ui.theme.LightGray
import org.example.project.ui.theme.TaskConvertAIAppTheme
import org.example.project.ui.viewComponents.GroupScreenComponents.AdminMembersList
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    navController: NavController,
    viewModel: CreateGroupViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { CreateGroupTopBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            GroupForm(state = state, viewModel = viewModel, modifier = Modifier.weight(1f))
            CreateButton {
                viewModel.createGroup { navController.popBackStack() }
            }
        }

        if (state.addDialogVisible) {
            AddParticipantDialog(state, viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateGroupTopBar(navController: NavController) {
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

@Composable
private fun GroupForm(
    state: CreateGroupUiState,
    viewModel: CreateGroupViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(12.dp))
        Text("Создание группы", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(24.dp))

        // Название
        Text("Название", style = MaterialTheme.typography.bodyLarge)
        OutlinedTextField(
            value = state.groupName,
            onValueChange = viewModel::onGroupNameChange,
            placeholder = {
                Text(
                    "Work Group",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Описание
        Text("Описание", style = MaterialTheme.typography.bodyLarge)
        OutlinedTextField(
            value = state.description,
            onValueChange = viewModel::onDescriptionChange,
            placeholder = {
                Text(
                    "пара слов о группе",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            },
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

        AdminMembersList(
            state.participants,
            { id -> viewModel.removeParticipant(id) },
            { viewModel.addParticipant() })
    }
}

@Composable
private fun CreateButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(52.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text("Создать")
    }
}

@Composable
private fun AddParticipantDialog(state: CreateGroupUiState, viewModel: CreateGroupViewModel) {
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

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
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
