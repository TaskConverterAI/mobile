package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat

import org.example.project.ui.TaskConvertAIApp
import org.example.project.ui.theme.TaskConvertAIAppTheme

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        enableEdgeToEdge()
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            App()
//        }
//    }
//}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window,false)
        initializeAndroid(this)
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
