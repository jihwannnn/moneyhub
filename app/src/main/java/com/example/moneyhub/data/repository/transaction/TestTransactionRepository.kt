package com.example.moneyhub.data.repository.transaction

import com.example.moneyhub.model.Category
import com.example.moneyhub.model.Transaction
import javax.inject.Inject

class TestTransactionRepository @Inject constructor() : TransactionRepository {
    override suspend fun addTransaction(
        gid: String,
        transaction: Transaction
    ): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun modifyTransaction(
        gid: String,
        transaction: Transaction
    ): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun deleteTransaction(
        gid: String,
        tid: String
    ): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getTransactions(
        gid: String,
        verified: Boolean
    ): Result<List<Transaction>> {
        return Result.success(emptyList())
    }

    override suspend fun getTransactionsByDate(
        gid: String,
        date: Long
    ): Result<List<Transaction>> {
        return Result.success(emptyList())
    }

    override suspend fun getTransactionsByMonth(
        gid: String,
        yearMonth: Long
    ): Result<List<Transaction>> {
        return Result.success(emptyList())
    }

    override suspend fun getCategory(gid: String): Result<Category> {
        return Result.success(Category())
    }

    override suspend fun saveCategory(gid: String, category: Category): Result<Unit> {
        return Result.success(Unit)
    }
}