package com.example.moneyhub.data.repository.transaction

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.moneyhub.model.Transaction

class TransactionRepositoryImpl : TransactionRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override suspend fun addTransaction(
        gid: String,
        transaction: Transaction
    ): Result<String> {
        TODO()
    }

    override suspend fun deleteTransaction(
        gid: String,
        tid: String
    ): Result<Unit> {
        TODO()
    }

    override suspend fun updateTransactionToHistory(
        gid: String,
        tid: String,
        receiptUrl: String?
    ): Result<Unit> {
        TODO()
    }

    override suspend fun getTransactions(
        gid: String,
        verified: Boolean
    ): Result<List<Transaction>> {
        TODO()
    }

    override suspend fun getTransactionsByDate(
        gid: String,
        startDate: Long,
        endDate: Long
    ): Result<List<Transaction>> {
        TODO()
    }

    override suspend fun getTransactionsByCategory(
        gid: String,
        category: String
    ): Result<List<Transaction>> {
        TODO()
    }
}