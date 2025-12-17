package org.example.project.ui.viewComponents.noteScreenComponents

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import org.example.project.data.commonData.Task
import org.example.project.ui.viewComponents.commonComponents.BlockType
import org.example.project.ui.viewComponents.commonComponents.ColorBlock

@Composable
fun TaskChoosingItem(
    task: Task,
    isEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onTitleEdit: ((String) -> Unit)? = null,
    onDescriptionEdit: ((String) -> Unit)? = null
) {
    val useColor = if (isEnabled) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Gray
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Switch(
            checked = isEnabled,
            onCheckedChange = onEnabledChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = useColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = useColor
            ),
            modifier = Modifier.rotate(90f)
        )

        ColorBlock(
            blockType = BlockType.SIMPLE_TASK,
            task = task,
            backgroundColor = useColor,
            onTitleEdit = onTitleEdit,
            onContentEdit = onDescriptionEdit
        )
    }
}
