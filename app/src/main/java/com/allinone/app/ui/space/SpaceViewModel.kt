package com.allinone.app.ui.space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allinone.app.data.AppContainer
import com.allinone.app.data.db.entity.SpaceItemEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SpaceViewModel(private val container: AppContainer) : ViewModel() {

    private val repo = container.spaceRepository

    private val _currentFolderId = MutableStateFlow<Long?>(null)
    val currentFolderId: StateFlow<Long?> = _currentFolderId

    /** Ordered chain of open folders from root to current (empty when at root). */
    private val _folderPath = MutableStateFlow<List<SpaceItemEntity>>(emptyList())
    val folderPath: StateFlow<List<SpaceItemEntity>> = _folderPath

    val items: StateFlow<List<SpaceItemEntity>> = MutableStateFlow(emptyList())

    private var saveJob: Job? = null

    init {
        viewModelScope.launch {
            _currentFolderId.collect { folderId ->
                val flow = if (folderId == null) repo.observeRoot() else repo.observeInFolder(folderId)
                flow.collect { (items as MutableStateFlow).value = it }
            }
        }
    }

    fun openFolder(folderId: Long) {
        viewModelScope.launch {
            val folder = repo.getById(folderId)
            if (folder != null) {
                _folderPath.value = _folderPath.value + folder
                _currentFolderId.value = folderId
            }
        }
    }

    fun navigateBack() {
        val path = _folderPath.value
        if (path.isEmpty()) return
        val newPath = path.dropLast(1)
        _folderPath.value = newPath
        _currentFolderId.value = newPath.lastOrNull()?.id
    }

    fun navigateToRoot() {
        _folderPath.value = emptyList()
        _currentFolderId.value = null
    }

    suspend fun getItem(id: Long) = repo.getById(id)

    suspend fun createFolder(title: String): Long =
        repo.createFolder(title, _currentFolderId.value)

    suspend fun addContent(type: String, title: String, contentRef: String, icon: String? = null): Long =
        repo.addContent(type, title, contentRef, _currentFolderId.value, icon = icon)

    /** Debounced position save during drag. */
    fun updatePosition(item: SpaceItemEntity) {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(150)
            repo.update(item)
        }
    }

    suspend fun deleteItem(item: SpaceItemEntity) {
        repo.delete(item)
    }

    suspend fun renameItem(item: SpaceItemEntity, newTitle: String) {
        repo.update(item.copy(title = newTitle, updatedAt = System.currentTimeMillis()))
    }

    fun search(query: String): StateFlow<List<SpaceItemEntity>> {
        val result = MutableStateFlow<List<SpaceItemEntity>>(emptyList())
        viewModelScope.launch {
            repo.search(query).collect { result.value = it }
        }
        return result
    }
}
