package com.example.moneyhub.model.sessions

import com.example.moneyhub.model.Transaction

object TransactionSession {
    private var currentTransaction: Transaction? = null

    fun setTransaction(transaction: Transaction) {
        currentTransaction = transaction
    }

    fun getCurrentTransaction(): Transaction = currentTransaction ?: Transaction()

    fun clearCurrentTransaction() {
        currentTransaction = null
    }
}