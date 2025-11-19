package org.example.project.ui.viewComponents.GroupScreenComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.LightGray

@Composable
fun MembersList(users: List<String>) {
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
        users.forEachIndexed { index, user ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(user, modifier = Modifier.weight(1f))
            }
            HorizontalDivider(color = LightGray.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun AdminMembersList(users: List<String>,
                onRemove: (idx: Int) -> Unit,
                onAddMember: () -> Unit) {
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
        users.forEachIndexed { index, user ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(user, modifier = Modifier.weight(1f))
                IconButton(onClick = { onRemove(index) }) {
                    Icon(Icons.Default.Close, contentDescription = "remove", tint = MaterialTheme.colorScheme.error)
                }
            }
            HorizontalDivider(color = LightGray.copy(alpha = 0.5f))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .clickable { onAddMember() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("+ добавить участника", color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
        }
    }
}

