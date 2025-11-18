package org.example.project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.commonData.Note
import org.example.project.data.database.repository.NoteRepository
import org.example.project.data.sync.SyncState

/**
 * ViewModel для работы с заметками и синхронизацией
 */
class NoteSyncViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Загрузить заметки из БД
        loadNotes()

        // Подписаться на состояние синхронизации
        viewModelScope.launch {
            noteRepository.getSyncState()?.collect { state ->
                _syncState.value = state
            }
        }
    }

    /**
     * Загрузить заметки из локальной БД
     */
    private fun loadNotes() {
        viewModelScope.launch {
            noteRepository.getAllNotes().collect { notesList ->
                _notes.value = notesList
            }
        }
    }

    /**
     * Синхронизировать все заметки с сервером
     */
    fun syncNotes() {
        viewModelScope.launch {
            val result = noteRepository.syncWithServer()
            result.fold(
                onSuccess = {
                    _errorMessage.value = null
                    // Заметки автоматически обновятся через Flow
                },
                onFailure = { error ->
                    _errorMessage.value = "Ошибка синхронизации: ${error.message}"
                }
            )
        }
    }

    /**
     * Создать новую заметку с синхронизацией
     */
    fun createNoteWithSync(note: Note) {
        viewModelScope.launch {
            try {
                noteRepository.insertNoteAndSync(note)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка создания заметки: ${e.message}"
            }
        }
    }

    /**
     * Создать новую заметку без синхронизации (только локально)
     */
    fun createNoteLocally(note: Note) {
        viewModelScope.launch {
            try {
                noteRepository.insertNote(note)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка создания заметки: ${e.message}"
            }
        }
    }

    /**
     * Обновить заметку с синхронизацией
     */
    fun updateNoteWithSync(noteId: Long, note: Note) {
        viewModelScope.launch {
            try {
                noteRepository.updateNoteAndSync(noteId, note)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка обновления заметки: ${e.message}"
            }
        }
    }

    /**
     * Обновить заметку без синхронизации (только локально)
     */
    fun updateNoteLocally(noteId: Long, note: Note) {
        viewModelScope.launch {
            try {
                noteRepository.updateNote(noteId, note)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка обновления заметки: ${e.message}"
            }
        }
    }

    /**
     * Удалить заметку с синхронизацией
     */
    fun deleteNoteWithSync(noteId: Long) {
        viewModelScope.launch {
            try {
                noteRepository.deleteNoteAndSync(noteId)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка удаления заметки: ${e.message}"
            }
        }
    }

    /**
     * Удалить заметку без синхронизации (только локально)
     */
    fun deleteNoteLocally(noteId: Long) {
        viewModelScope.launch {
            try {
                noteRepository.deleteNote(noteId)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка удаления заметки: ${e.message}"
            }
        }
    }

    /**
     * Загрузить конкретную заметку с сервера
     */
    fun pullNoteFromServer(noteId: Long) {
        viewModelScope.launch {
            val result = noteRepository.pullNoteFromServer(noteId)
            result.fold(
                onSuccess = {
                    _errorMessage.value = null
                },
                onFailure = { error ->
                    _errorMessage.value = "Ошибка загрузки заметки: ${error.message}"
                }
            )
        }
    }

    /**
     * Отправить конкретную заметку на сервер
     */
    fun pushNoteToServer(noteId: Long) {
        viewModelScope.launch {
            val result = noteRepository.pushNoteToServer(noteId)
            result.fold(
                onSuccess = {
                    _errorMessage.value = null
                },
                onFailure = { error ->
                    _errorMessage.value = "Ошибка отправки заметки: ${error.message}"
                }
            )
        }
    }

    /**
     * Очистить сообщение об ошибке
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Сбросить состояние синхронизации
     */
    fun resetSyncState() {
        _syncState.value = SyncState.Idle
    }
}

