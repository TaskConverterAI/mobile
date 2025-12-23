package org.example.project.ui.screens.groupsScreen.detailedGroupScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.example.project.ui.theme.LightGray
import org.example.project.ui.viewComponents.GroupScreenComponents.AdminMembersList
import org.example.project.ui.viewComponents.GroupScreenComponents.MembersList
import org.example.project.ui.screens.statusToast.*

@Serializable
data class DetailGroupScreenArgs(val groupId: Long)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailGroupScreen(viewModel: DetailedGroupViewModel, navController: NavController)
{
    val detailsUiState by viewModel.groupDetails.collectAsState()

    var toastMessage by remember { mutableStateOf<String?>(null) }
    var toastType by remember { mutableStateOf(StatusType.ERROR) }
    var toastKey by remember { mutableStateOf(0) }

    LaunchedEffect(detailsUiState.error) {
        if (detailsUiState.error != null) {
            toastMessage = detailsUiState.error
            toastType = StatusType.ERROR
            toastKey++
            viewModel.clearError()
        }
    }

    LaunchedEffect(toastKey) {
        if (toastMessage != null) {
            delay(ToastDuration.LONG.millis)
            toastMessage = null
        }
    }

    if (detailsUiState.successLeave) {
        navController.navigate("groups")
    }

    toastMessage?.let { message ->
        StatusToast(
            type = toastType,
            message = message,
            duration = ToastDuration.LONG,
            onDismiss = { toastMessage = null }
        )
    }

    Scaffold(
        topBar = { DetailsGroupTopBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            GroupDetailsForm(viewModel = viewModel, modifier = Modifier.weight(1f))
        }
    }

    if (detailsUiState.showLeaveDialog) {
        if (detailsUiState.isAdmin && detailsUiState.users.size > 1) {
            LeaveAdminGroupDialog(detailsUiState.name,{
                viewModel.setLeave(false)
            },{ accessor_email ->
                viewModel.leaveMyOwnGroup(accessor_email)
            })
        } else {
            LeaveGroupDialog(detailsUiState.name, {
                viewModel.setLeave(false)
            }, {
                viewModel.leaveGroup()
            })
        }
    }

    if (detailsUiState.showAddMemberDialog) {
        AddMemberDialog(
            onDismiss = {
                viewModel.dismissAddMemberDialog()
            },
            onConfirm = { email ->
                viewModel.addParticipantByEmail(email)
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsGroupTopBar(navController: NavController) {
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
private fun GroupDetailsForm(
    viewModel: DetailedGroupViewModel,
    modifier: Modifier = Modifier
) {
    val detailsUiState by viewModel.groupDetails.collectAsState()

    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(12.dp))
        Text(detailsUiState.name, style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(24.dp))

        if (detailsUiState.isAdmin) {
            AdminMembersList(
                detailsUiState.users.map { user -> user.email },
                { id -> viewModel.removeParticipant(id)
                },
                { viewModel.addParticipant() })
        } else {
            MembersList(detailsUiState.users.map { user -> user.email })
        }

        Spacer(Modifier.height(20.dp))

        OutlinedButton(
            onClick = { viewModel.setLeave(true)},
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.End),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.error
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Покинуть группу")
        }

    }


}

