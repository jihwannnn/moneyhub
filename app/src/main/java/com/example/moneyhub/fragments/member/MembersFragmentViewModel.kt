package com.example.moneyhub.fragments.member

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.group.GroupRepository
import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.Membership
import com.example.moneyhub.model.Role
import com.example.moneyhub.model.sessions.CurrentUserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MembersFragmentViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _members = MutableStateFlow<List<Membership>>(emptyList())
    val members: StateFlow<List<Membership>> = _members.asStateFlow()

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    private val _currentGroupName = MutableStateFlow("")
    val currentGroupName: StateFlow<String> = _currentGroupName.asStateFlow()

    private val _currentGroupId = MutableStateFlow("")

    init {
        loadCurrentUser()
        loadCurrentGroupInfo()
    }


    private fun loadCurrentUser() {
        _currentUser.value = CurrentUserSession.getCurrentUser()
        loadMembers()
    }

    private fun loadCurrentGroupInfo() {
        _currentUser.value?.let { user ->
            _currentGroupId.value = user.currentGid
            _currentGroupName.value = user.currentGname
        }
    }

    private fun loadMembers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                _currentUser.value?.let { user ->
                    groupRepository.getGroupMembers(user.currentGid).fold(
                        onSuccess = { membersList ->
                            _members.value = membersList
                            _uiState.update { it.copy(
                                isLoading = false,
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

    fun promoteMember(uid: String, newRole: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                _currentUser.value?.let { currentUser ->
                    val membership = Membership(
                        uid = uid,
                        gid = currentUser.currentGid,
                        role = Role.fromName(newRole)
                    )
                    groupRepository.promoteMember(membership, currentUser).fold(
                        onSuccess = {
                            loadMembers() // 멤버 목록 새로고침
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
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                ) }
            }
        }
    }

    fun demoteMember(uid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                _currentUser.value?.let { currentUser ->
                    val membership = Membership(
                        uid = uid,
                        gid = currentUser.currentGid
                    )
                    groupRepository.demoteMember(membership).fold(
                        onSuccess = {
                            loadMembers() // 멤버 목록 새로고침
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
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                ) }
            }
        }
    }

    fun leaveGroup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                _currentUser.value?.let { currentUser ->
                    groupRepository.leaveGroup(currentUser.currentGid, currentUser).fold(
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
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                ) }
            }
        }
    }

    fun copyGroupId(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Group ID", _currentGroupId.value)
        clipboard.setPrimaryClip(clip)
    }
}