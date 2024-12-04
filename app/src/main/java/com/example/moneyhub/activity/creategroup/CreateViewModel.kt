package com.example.moneyhub.activity.creategroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.group.GroupRepository
import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.sessions.CurrentUserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _currentUser.value = CurrentUserSession.getCurrentUser()
        }
    }

    fun createGroup(name: String) {
        if (!validateInput(name)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val user = currentUser.value
                if (user != null) {
                    groupRepository.createGroup(name, user).fold(
                        onSuccess = {
                            _uiState.update { it.copy(
                                isLoading = false,
                                isSuccess = true,
                                error = null
                            ) }
                        },
                        onFailure = { throwable ->
                            _uiState.update { it.copy(
                                isLoading = false,
                                error = throwable.message
                            ) }
                        }
                    )
                } else {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "로그인이 필요합니다"
                    ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                ) }
            }
        }
    }

    private fun validateInput(name: String): Boolean = when {
        name.isBlank() -> {
            _uiState.update { it.copy(error = "그룹 이름을 입력해주세요") }
            false
        }
        else -> true
    }
}