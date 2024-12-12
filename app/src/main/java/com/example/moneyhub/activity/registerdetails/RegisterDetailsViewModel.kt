package com.example.moneyhub.activity.registerdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyhub.common.UiState
import com.example.moneyhub.data.repository.transaction.TransactionRepository
import com.example.moneyhub.model.Category
import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.Transaction
import com.example.moneyhub.model.sessions.CurrentUserSession
import com.example.moneyhub.model.sessions.RegisterTransactionSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterDetailsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    private val _currentTransaction = MutableStateFlow(Transaction())
    val currentTransaction: StateFlow<Transaction> = _currentTransaction.asStateFlow()

    private val _category = MutableStateFlow(Category())
    val category: StateFlow<Category> = _category.asStateFlow()

    init {
        loadCurrentUser()
        loadCategory()
        loadCurrentTransaction()
    }

    private fun loadCurrentUser() {
        _currentUser.value = CurrentUserSession.getCurrentUser()
    }

    private fun loadCurrentTransaction() {
        _currentTransaction.value = RegisterTransactionSession.getCurrentTransaction()
    }

    private fun loadCategory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                _currentUser.value?.let { user ->
                    transactionRepository.getCategory(user.currentGid).fold(
                        onSuccess = { category->
                            _category.value = category
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

    fun saveTransaction(
        title: String,
        category: String,
        type: Boolean,
        amount: Long,
        content: String,
        payDate: Long
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            _currentUser.value?.let { currentUser ->
                val transaction = _currentTransaction.value.copy(
                    gid = currentUser.currentGid,
                    title = title,
                    category = category,
                    type = type,
                    amount = amount,
                    content = content,
                    payDate = payDate,
                    authorId = currentUser.id,
                    authorName = currentUser.name
                )

                if(transaction.tid.isEmpty()) {
                    val nt = transaction.copy(
                        createdAt = System.currentTimeMillis()
                    )

                    transactionRepository.addTransaction(currentUser.currentGid, nt).fold(
                        onSuccess = {
                            RegisterTransactionSession.clearCurrentTransaction()
                            _currentTransaction.value = Transaction()
                            _uiState.update { it.copy(isLoading = false, isSuccess = true, error = null) }
                        },
                        onFailure = { throwable ->
                            _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                        }
                    )
                }

                else {
                    transactionRepository.modifyTransaction(currentUser.currentGid, transaction).fold(
                        onSuccess = {
                            RegisterTransactionSession.clearCurrentTransaction()
                            _currentTransaction.value = Transaction()
                            _uiState.update { it.copy(isLoading = false, isSuccess = true, error = null) }
                        },
                        onFailure = { throwable ->
                            _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                        }
                    )
                }
            }
        }
    }

    // RegisterDetailsViewModel.kt
    fun saveCategory(newCategory: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                _currentUser.value?.let { user ->
                    // 현재 카테고리 리스트에 새 카테고리 추가
                    val updatedCategories = _category.value.category.toMutableList().apply {
                        add(newCategory)
                    }
                    val newCategoryData = Category(
                        gid = user.currentGid,
                        category = updatedCategories
                    )

                    // Repository를 통해 저장
                    transactionRepository.saveCategory(user.currentGid, newCategoryData).fold(
                        onSuccess = {
                            _category.value = newCategoryData
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
}