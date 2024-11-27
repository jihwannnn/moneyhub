package com.example.moneyhub.data.repository.transaction

import com.example.moneyhub.data.model.Transaction

interface TransactionRepository {
    suspend fun addTransaction(gid: String, transaction: Transaction): Result<String> // tid 반환
    suspend fun deleteTransaction(gid: String, tid: String): Result<Unit>
    suspend fun updateTransactionToHistory(gid: String, tid: String, receiptUrl: String?): Result<Unit>
    suspend fun getTransactions(gid: String, verified: Boolean): Result<List<Transaction>>
    suspend fun getTransactionsByDate(gid: String, startDate: Long, endDate: Long): Result<List<Transaction>>
    suspend fun getTransactionsByCategory(gid: String, category: String): Result<List<Transaction>>
}
