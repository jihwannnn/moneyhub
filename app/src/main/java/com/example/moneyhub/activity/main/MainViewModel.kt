package com.example.moneyhub.activity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
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
class MainViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val _currentDestination = MutableStateFlow<Int>(0)
    val currentDestination: LiveData<Int> get() = _currentDestination.asLiveData()

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        _currentUser.value = CurrentUserSession.getCurrentUser()
    }

    fun updateCurrentDestination(destinationId: Int) {
        _currentDestination.value = destinationId
    }

    fun deleteCurrentGroup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val currentUser = _currentUser.value ?: throw Exception("User not found")
                val currentGroupId = currentUser.currentGid

                if (currentGroupId.isEmpty()) {
                    throw Exception("No group selected")
                }

                groupRepository.deleteGroup(currentGroupId, currentUser).fold(
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
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                ) }
            }
        }
    }
}