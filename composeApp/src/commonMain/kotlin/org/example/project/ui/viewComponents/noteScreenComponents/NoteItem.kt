package org.example.project.ui.viewComponents.noteScreenComponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import org.example.project.data.commonData.Note
import org.example.project.ui.viewComponents.commonComponents.BlockType
import org.example.project.ui.viewComponents.commonComponents.ColorBlock

@Composable
fun NoteItem(note: Note) {
    ColorBlock(
        blockType = BlockType.SIMPLE_NOTE,
        note = note,
        backgroundColor = note.color,
        modifier = Modifier.fillMaxWidth()
    )
}
