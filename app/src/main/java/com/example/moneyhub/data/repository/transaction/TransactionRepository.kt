package com.example.moneyhub.data.repository.transaction

import com.example.moneyhub.model.Category
import com.example.moneyhub.model.Transaction

interface TransactionRepository {
    // 트랜잭션 생성
    suspend fun addTransaction(gid: String, transaction: Transaction): Result<Unit>

    // 트랜잭션 수정
    suspend fun modifyTransaction(gid: String, transaction: Transaction): Result<Unit>

    //트랜잭션 삭제
    suspend fun deleteTransaction(gid: String, tid: String): Result<Unit>

    // 예산/내역 가져오기
    suspend fun getTransactions(gid: String, verified: Boolean): Result<List<Transaction>>

    // 특정 날짜 내역 가져오기
    suspend fun getTransactionsByDate(gid: String, date: Long): Result<List<Transaction>>

    // 특정 달 내역 가져오기
    suspend fun getTransactionsByMonth(gid: String, yearMonth: Long): Result<List<Transaction>>

    // 카테고리 가져오기
    suspend fun getCategory(gid: String): Result<Category>

    //카테고리 저장하기
    suspend fun saveCategory(gid: String, category: Category): Result<Unit>
}
