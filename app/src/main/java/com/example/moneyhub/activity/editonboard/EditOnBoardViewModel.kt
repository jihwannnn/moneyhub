package com.example.moneyhub.activity.editonboard

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.board.BoardRepository
import com.example.moneyhub.model.Post
import com.example.moneyhub.model.sessions.PostSession
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

@HiltViewModel
class EditOnBoardViewModel @Inject constructor(
    private val repository: BoardRepository,
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _currentPost = MutableStateFlow<Post?>(null)
    val currentPost: StateFlow<Post?> = _currentPost.asStateFlow()

    init {
        // 초기화 로직이 필요할 경우 추가
    }

    fun fetchPost(post: Post) {
        viewModelScope.launch {
            _currentPost.value = post
        }
    }

    fun updatePost(newTitle: String, newContent: String, newImageUri: Uri? = null) {
        val post = _currentPost.value ?: run {
            _uiState.update { it.copy(error = "게시글 정보를 찾을 수 없습니다.") }
            return
        }

        if (newTitle.isBlank() || newContent.isBlank()) {
            _uiState.update { it.copy(error = "제목과 내용을 모두 입력해주세요.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val imageUrl = if (newImageUri != null) {
                    uploadImageToFirebase(newImageUri)
                } else {
                    post.imageUrl // 기존 이미지 URL 유지
                }

                val updatedPost = post.copy(
                    title = newTitle,
                    content = newContent,
                    imageUrl = imageUrl
                )

                repository.updatePost(updatedPost).fold(
                    onSuccess = {
                        _currentPost.value = updatedPost
                        _uiState.update { it.copy(isLoading = false, isSuccess = true, error = null) }
                        // PostSession 업데이트
                        PostSession.setPost(updatedPost)
                    },
                    onFailure = { throwable ->
                        _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private suspend fun uploadImageToFirebase(imageUri: Uri): String {
        return suspendCancellableCoroutine { cont ->
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        cont.resume(uri.toString(), null)
                    }.addOnFailureListener { exception ->
                        cont.resumeWithException(exception)
                    }
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
        }
    }
}
