package org.example.project.ui.screens.notesScreen.conditionScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

import org.example.project.ui.theme.DarkGray
import taskconvertaiapp.composeapp.generated.resources.Res


@Composable
fun EmptyMainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = Res.getUri("files/notes/emptyScreen/Illustration.svg"),
            contentDescription = "Empty Illustration",
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.size(24.dp))

        Text("Давай начнём",
            style = MaterialTheme.typography.displayLarge)

        Spacer(modifier = Modifier.size(12.dp))

        Text("Любое дело начинается с одной мысли",
            style = MaterialTheme.typography.bodySmall,
            color = DarkGray,
            modifier = Modifier.widthIn(max = 200.dp),
            textAlign = TextAlign.Center
        )

        AsyncImage(
            model = Res.getUri("files/notes/emptyScreen/Arrow.svg"),
            contentDescription = "Arrow Illustration",
            modifier = Modifier.size(300.dp).offset(y = 12.dp)
        )
    }
}
