package com.example.moneyhub.activity.postonboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostOnBoardViewModel @Inject constructor(
    private val repository: PostRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(UiState.INITIAL)
    val uiState = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Form States
    private val _title = savedStateHandle.getStateFlow("title", "")
    val title = _title

    private val _content = savedStateHandle.getStateFlow("content", "")
    val content = _content

    private val _imageUri = savedStateHandle.getStateFlow<String?>("imageUri", null)
    val imageUri = _imageUri

    // Update Functions
    fun updateTitle(title: String) {
        savedStateHandle["title"] = title
    }

    fun updateContent(content: String) {
        savedStateHandle["content"] = content
    }

    fun updateImageUri(value: String?) {
        savedStateHandle["imageUri"] = value
    }

    // Post submission logic
    fun post(authorId: String, authorName: String, groupId: String) {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = UiState.LOADING

            val post = Post(
                pid = System.currentTimeMillis().toString(), // Unique ID
                gid = groupId,
                title = title.value,
                authorId = authorId,
                authorName = authorName,
                imageUrl = imageUri.value,
                commentCount = 0,
                createdAt = System.currentTimeMillis()
            )

            repository.createPost(post).onSuccess {
                _uiState.value = UiState.SUCCESS
            }.onFailure { error ->
                _uiState.value = UiState.ERROR
                _errorMessage.value = error.message
            }
        }
    }

    private fun validateInputs(): Boolean = when {
        title.value.isBlank() -> {
            _errorMessage.value = "제목을 입력해주세요"
            false
        }

        content.value.isBlank() -> {
            _errorMessage.value = "내용을 입력해주세요"
            false
        }

        else -> true
    }
}