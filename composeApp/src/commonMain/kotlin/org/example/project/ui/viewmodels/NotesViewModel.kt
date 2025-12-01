package org.example.project.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.example.project.data.commonData.Comment
import org.example.project.data.commonData.Note
import org.example.project.data.database.DatabaseProvider
import org.example.project.data.database.repository.NoteRepository

class NotesViewModel(
    private val noteRepository: NoteRepository = DatabaseProvider.getNoteRepository()
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadNotes()
    }

    /**
     * Загрузить все заметки
     */
    private fun loadNotes() {
        viewModelScope.launch {
            _isLoading.value = true
            noteRepository.getAllNotes()
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { notesList ->
                    _notes.value = notesList
                    _isLoading.value = false
                }
        }
    }

    /**
     * Добавить новую заметку
     * @param note - заметка с группой (Group объект будет автоматически преобразован в groupId)
     */
    fun addNote(note: Note) {
        viewModelScope.launch {
            try {
                noteRepository.insertNote(note)
                // Заметки обновятся автоматически через Flow
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * Обновить существующую заметку
     * @param noteId - ID заметки для обновления
     * @param note - обновлённые данные заметки
     */
    fun updateNote(noteId: Long, note: Note) {
        viewModelScope.launch {
            try {
                noteRepository.updateNote(note)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * Удалить заметку
     * @param noteId - ID заметки для удаления
     */
    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            try {
                noteRepository.deleteNote(noteId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * Добавить комментарий к заметке
     * @param noteId - ID заметки
     * @param comment - комментарий для добавления
     */
    fun addCommentToNote(noteId: Long, comment: Comment) {
        viewModelScope.launch {
            try {
                noteRepository.addCommentToNote(noteId, comment)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * Загрузить заметки конкретной группы
     * @param groupId - ID группы
     */
    fun getNotesByGroup(groupId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            noteRepository.getNotesByGroup(groupId)
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { notesList ->
                    _notes.value = notesList
                    _isLoading.value = false
                }
        }
    }

    /**
     * Получить конкретную заметку по ID
     * @param noteId - ID заметки
     * @return Note с полными деталями (группа, задачи, комментарии)
     */
    suspend fun getNoteById(noteId: Long): Note? {
        return try {
            noteRepository.getNoteById(noteId)
        } catch (e: Exception) {
            _error.value = e.message
            null
        }
    }

    fun clearError() {
        _error.value = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                NotesViewModel()
            }
        }
    }
}


