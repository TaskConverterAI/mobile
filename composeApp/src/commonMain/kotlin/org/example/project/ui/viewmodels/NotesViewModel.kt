package org.example.project.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.AppDependencies
import org.example.project.data.auth.AuthRepository
import org.example.project.data.commonData.Comment
import org.example.project.data.commonData.Note
import org.example.project.data.database.repository.GroupRepository
import org.example.project.data.database.repository.NoteRepository

class NotesViewModel(
    private val noteRepository: NoteRepository,
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val logger = Logger.withTag("NotesViewModel")

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _allNotes = MutableStateFlow<List<Note>>(emptyList())

    private val _groups = MutableStateFlow<List<org.example.project.data.commonData.Group>>(emptyList())
    val groups: StateFlow<List<org.example.project.data.commonData.Group>> = _groups.asStateFlow()

    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    val selectedGroupId: StateFlow<Long?> = _selectedGroupId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Загрузить все заметки
     */
    fun loadNotes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Logger.d("NotesViewModel") { "Начало загрузки заметок" }
                val userId = authRepository.getUserIdByToken()
                Logger.d("NotesViewModel") { "userId = $userId" }

                val response = noteRepository.getAllNotes(userId)
                Logger.d("NotesViewModel") { "получено ${response?.size ?: 0} заметок" }

                val allLoadedNotes = mutableListOf<Note>()

                if (response != null) {
                    allLoadedNotes.addAll(response.filter { note -> note.groupId == null })
                    Logger.d("NotesViewModel") { "добавлено ${allLoadedNotes.size} заметок без группы" }
                }

                val groups = groupRepository.getAllGroups(userId = userId)
                Logger.d("NotesViewModel") { "получено ${groups?.size ?: 0} групп" }

                if (groups != null) {
                    _groups.value = groups
                }

                groups?.forEach { group ->
                    val groupNotes = noteRepository.getNotesByGroup(group.id)
                    if (groupNotes != null) {
                        allLoadedNotes.addAll(groupNotes)
                        Logger.d("NotesViewModel") { "добавлено ${groupNotes.size} заметок из группы ${group.name}" }
                    }
                }

                _allNotes.value = allLoadedNotes
                applyFilter()

                Logger.d("NotesViewModel") { "Итого загружено ${_allNotes.value.size} заметок" }
            } catch (e: Exception) {
                Logger.e("NotesViewModel", e) { "Ошибка загрузки - ${e.message}" }
                _error.value = e.message
            } finally {
                _isLoading.value = false
                Logger.d("NotesViewModel") { "Загрузка завершена, isLoading = false" }
            }
        }
    }

    /**
     * Применить фильтр по группе
     */
    private fun applyFilter() {
        _notes.value = if (_selectedGroupId.value == null) {
            _allNotes.value
        } else {
            _allNotes.value.filter { it.groupId == _selectedGroupId.value }
        }
        Logger.d("NotesViewModel") { "Фильтр применен: ${_notes.value.size} заметок" }
    }

    /**
     * Выбрать фильтр по группе
     */
    fun selectGroup(groupId: Long?) {
        _selectedGroupId.value = groupId
        applyFilter()
    }

    /**
     * Добавить новую заметку
     * @param note - заметка с группой (Group объект будет автоматически преобразован в groupId)
     */
    fun addNote(note: Note) {
        viewModelScope.launch {
            try {
                val userId = authRepository.getUserIdByToken()
                noteRepository.insertNote(userId, note)
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
    fun updateNote(note: Note) {
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

    fun deleteCommentFromNote(commentId: Long) {
        viewModelScope.launch {
            try {
                noteRepository.deleteCommentFromNote(commentId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }



//    /**
//     * Загрузить заметки конкретной группы
//     * @param groupId - ID группы
//     */
//    fun getNotesByGroup(groupId: Long) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            noteRepository.getNotesByGroup(groupId)
//                .catch { e ->
//                    _error.value = e.message
//                    _isLoading.value = false
//                }
//                .collect { notesList ->
//                    _notes.value = notesList
//                    _isLoading.value = false
//                }
//        }
//    }

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
                NotesViewModel(
                    AppDependencies.container.noteRepository,
                    AppDependencies.container.authRepository,
                    AppDependencies.container.groupRepository
                )
            }
        }
    }
}
