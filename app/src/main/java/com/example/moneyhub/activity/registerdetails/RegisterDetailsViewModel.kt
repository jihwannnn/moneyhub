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
        loadCurrentTransaction()
        loadCategory()
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

    fun createTransaction(
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
                val newTransaction = Transaction(
                    tid = System.currentTimeMillis().toString(),
                    gid = currentUser.currentGid,
                    title = title,
                    category = category,
                    type = type,
                    amount = amount,
                    content = content,
                    payDate = payDate,
                    verified = true,
                    authorId = currentUser.id,
                    authorName = currentUser.name,
                    createdAt = System.currentTimeMillis()
                )

                transactionRepository.addTransaction(currentUser.currentGid, newTransaction).fold(
                    onSuccess = {
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