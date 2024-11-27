package com.example.moneyhub.data.repository.transaction

import com.example.moneyhub.model.Transaction
import javax.inject.Inject

class TestTransactionRepository @Inject constructor() : TransactionRepository {
    override suspend fun addTransaction(gid: String, transaction: Transaction): Result<String> {
        return Result.success("")
    }

    override suspend fun deleteTransaction(gid: String, tid: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun updateTransactionToHistory(gid: String, tid: String, receiptUrl: String?): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getTransactions(gid: String, verified: Boolean): Result<List<Transaction>> {
        return Result.success(emptyList())
    }

    override suspend fun getTransactionsByDate(gid: String, startDate: Long, endDate: Long): Result<List<Transaction>> {
        return Result.success(emptyList())
    }

    override suspend fun getTransactionsByCategory(gid: String, category: String): Result<List<Transaction>> {
        return Result.success(emptyList())
    }
}