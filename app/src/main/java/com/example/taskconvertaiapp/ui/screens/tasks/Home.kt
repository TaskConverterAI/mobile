package com.example.taskconvertaiapp.ui.screens.tasks

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.taskconvertaiapp.ui.theme.TaskConvertAIAppTheme
@Composable
fun HomeScreeen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

    }
}



@Preview(showBackground = false)
@Composable
private fun BirthdayCardPreview() {
    TaskConvertAIAppTheme {
        HomeScreeen()
    }
}
