package com.example.moneyhub.activity.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.data.repository.auth.AuthRepository
import com.example.moneyhub.data.repository.group.GroupRepository
import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.UserGroup
import com.example.moneyhub.model.sessions.CurrentUserSession
import com.example.moneyhub.model.sessions.PostSession
import com.example.moneyhub.model.sessions.RegisterTransactionSession
import com.example.moneyhub.model.sessions.TransactionSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    private val _userGroups = MutableStateFlow<UserGroup?>(null)
    val userGroups: StateFlow<UserGroup?> = _userGroups.asStateFlow()

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    init {
        loadUser()
    }


    private fun loadUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            _currentUser.value = CurrentUserSession.getCurrentUser()
            _currentUser.value?.let { currentUser ->
                try {
                    groupRepository.getUserGroups(currentUser.id).fold(
                        onSuccess = { userGroups ->
                            _userGroups.value = userGroups
                            _uiState.update { it.copy(
                                isLoading = false,
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

    fun selectGroup(gid: String, gname: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                _currentUser.value?.let { currentUser ->
                    groupRepository.getUserMembership(gid, currentUser.id).fold(
                        onSuccess = { membership ->
                            val updatedUser = currentUser.copy(
                                currentGid = gid,
                                currentGname = gname,
                                role = membership.role
                            )

                            CurrentUserSession.setCurrentUser(updatedUser)
                            _currentUser.value = updatedUser

                            _uiState.update { it.copy(
                                isLoading = false,
                                successType = SuccessType.GROUP_SELECTED,
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
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                ) }
            }
        }
    }

    fun joinGroup(gid: String){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                _currentUser.value?.let { currentUser ->
                    groupRepository.joinGroup(gid, currentUser).fold(
                        onSuccess = {
                            loadUser()
                            _uiState.update { it.copy(
                                isLoading = false,
                                successType = SuccessType.GROUP_JOINED,
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
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                ) }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                authRepository.signOut().fold(
                    onSuccess = {
                        CurrentUserSession.clearCurrentUser()  // 로컬 세션 클리어
                        PostSession.clearCurrentPost()
                        RegisterTransactionSession.clearCurrentTransaction()
                        TransactionSession.clearCurrentTransaction()
                        _uiState.update { it.copy(
                            isLoading = false,
                            successType = SuccessType.SIGN_OUT,
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

    data class MyPageUiState(
        val isLoading: Boolean = false,
        val successType: SuccessType? = null,
        val error: String? = null
    )

    enum class SuccessType {
        GROUP_SELECTED,
        GROUP_JOINED,
        SIGN_OUT
    }
}