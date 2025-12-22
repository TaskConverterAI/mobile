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
import org.example.project.ui.screens.statusToast.StatusType

data class ToastMessage(
    val message: String,
    val type: StatusType
)

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

    private val _toastMessage = MutableStateFlow<ToastMessage?>(null)
    val toastMessage: StateFlow<ToastMessage?> = _toastMessage.asStateFlow()

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
                } else {
                    _error.value = "Не удалось загрузить заметки"
                    _toastMessage.value = ToastMessage(
                        message = "Не удалось загрузить заметки",
                        type = StatusType.ERROR
                    )
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
                    } else {
                        _error.value = "Не удалось загрузить заметки группы ${group.name}"
                        _toastMessage.value = ToastMessage(
                            message = "Не удалось загрузить заметки группы ${group.name}",
                            type = StatusType.ERROR
                        )
                    }
                }

                _allNotes.value = allLoadedNotes
                applyFilter()

                Logger.d("NotesViewModel") { "Итого загружено ${_allNotes.value.size} заметок" }

//                _toastMessage.value = ToastMessage(
//                    message = "Заметки загружены",
//                    type = StatusType.SUCCESS
//                )
            } catch (e: Exception) {
                Logger.e("NotesViewModel", e) { "Ошибка загрузки - ${e.message}" }
                _error.value = e.message
                _toastMessage.value = ToastMessage(
                    message = "Ошибка загрузки заметок: ${e.message}",
                    type = StatusType.ERROR
                )
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
                val res: Note? = noteRepository.insertNote(userId, note)

                if (res == null) {
                    _error.value = "Не удалось добавить заметку"
                    _toastMessage.value = ToastMessage(
                        message = "Не удалось добавить заметку",
                        type = StatusType.ERROR
                    )
                }

//                _toastMessage.value = ToastMessage(
//                    message = "Заметка добавлена",
//                    type = StatusType.SUCCESS
//                )
            } catch (e: Exception) {
                _error.value = e.message
                _toastMessage.value = ToastMessage(
                    message = "Ошибка добавления заметки: ${e.message}",
                    type = StatusType.ERROR
                )
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
                val res = noteRepository.updateNote(note)

                if (res == null) {
                    _error.value = "Не удалось обновить заметку"
                    _toastMessage.value = ToastMessage(
                        message = "Не удалось обновить заметку",
                        type = StatusType.ERROR
                    )
                }

//                _toastMessage.value = ToastMessage(
//                    message = "Заметка обновлена",
//                    type = StatusType.SUCCESS
//                )
            } catch (e: Exception) {
                _error.value = e.message
                _toastMessage.value = ToastMessage(
                    message = "Ошибка обновления заметки: ${e.message}",
                    type = StatusType.ERROR
                )
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

//                _toastMessage.value = ToastMessage(
//                    message = "Заметка удалена",
//                    type = StatusType.SUCCESS
//                )
            } catch (e: Exception) {
                _error.value = e.message
                _toastMessage.value = ToastMessage(
                    message = "Ошибка удаления заметки: ${e.message}",
                    type = StatusType.ERROR
                )
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
                val res = noteRepository.addCommentToNote(noteId, comment)

                if (res == null) {
                    _error.value = "Не удалось добавить комментарий"
                    _toastMessage.value = ToastMessage(
                        message = "Не удалось добавить комментарий",
                        type = StatusType.ERROR
                    )
                }

//                _toastMessage.value = ToastMessage(
//                    message = "Комментарий добавлен",
//                    type = StatusType.SUCCESS
//                )
            } catch (e: Exception) {
                _error.value = e.message
                _toastMessage.value = ToastMessage(
                    message = "Ошибка добавления комментария: ${e.message}",
                    type = StatusType.ERROR
                )
            }
        }
    }

    fun deleteCommentFromNote(commentId: Long) {
        viewModelScope.launch {
            try {
                noteRepository.deleteCommentFromNote(commentId)

//                _toastMessage.value = ToastMessage(
//                    message = "Комментарий удалён",
//                    type = StatusType.SUCCESS
//                )
            } catch (e: Exception) {
                _error.value = e.message
                _toastMessage.value = ToastMessage(
                    message = "Ошибка удаления комментария: ${e.message}",
                    type = StatusType.ERROR
                )
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
            val res: Note? = noteRepository.getNoteById(noteId)

            if (res == null) {
                _error.value = "Не удалось загрузить заметку"
                _toastMessage.value = ToastMessage(
                    message = "Не удалось загрузить заметку",
                    type = StatusType.ERROR
                )
            }

//            _toastMessage.value = ToastMessage(
//                message = "Заметка загружена",
//                type = StatusType.SUCCESS
//            )

            res
        } catch (e: Exception) {
            _error.value = e.message
            _toastMessage.value = ToastMessage(
                message = "Ошибка получения заметки: ${e.message}",
                type = StatusType.ERROR
            )
            null
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearToast() {
        _toastMessage.value = null
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
