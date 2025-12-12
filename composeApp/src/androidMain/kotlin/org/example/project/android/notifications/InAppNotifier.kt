package org.example.project.android.notifications

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object InAppNotifier {
    data class Message(val noteId: Long, val title: String, val body: String)

    private val _events = MutableSharedFlow<Message>(extraBufferCapacity = 8)
    val events: SharedFlow<Message> = _events

    fun push(message: Message) {
        _events.tryEmit(message)
    }
}

