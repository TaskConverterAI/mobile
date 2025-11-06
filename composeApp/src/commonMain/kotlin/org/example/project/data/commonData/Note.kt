package org.example.project.data.commonData

import androidx.compose.ui.graphics.Color
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

import org.example.project.ui.theme.PrimaryBase

data class Note @OptIn(ExperimentalTime::class) constructor(
    val title: String,
    val content: String,
    val geotag: String,
    val group: String,
    val comments: List<Comment>,
    val color: Color = PrimaryBase,
    val creationDate: Long = Clock.System.now().toEpochMilliseconds(),
    val contentMaxLines: Int = Int.MAX_VALUE)
