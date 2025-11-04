package com.example.taskconvertaiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat

import com.example.taskconvertaiapp.shared.ui.TaskConvertAIApp
import com.example.taskconvertaiapp.shared.ui.theme.TaskConvertAIAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window,false)
        enableEdgeToEdge()
        setContent {
            TaskConvertAIAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TaskConvertAIApp()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TaskConvertAIAppTheme {
        Greeting("Android")
    }
}
