package com.example.moneyhub.test.repo

import com.google.firebase.firestore.FirebaseFirestore

import com.example.moneyhub.model.Category
import com.example.moneyhub.model.Transaction
import com.example.moneyhub.utils.DateUtils
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor() : TransactionRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun addTransaction(
        gid: String,
        transaction: Transaction
    ): Result<Unit> {
        return try {

            // 트랜잭션 생성
            val transactionRef = db.collection("transactions_group")
                .document(gid)
                .collection("transactions")
                .document()

            // tid 할당
            val newTransaction = transaction.copy(
                tid = transactionRef.id
            )

            // 트랜잭션 저장
            transactionRef.set(newTransaction.toMap()).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun modifyTransaction(
        gid: String,
        transaction: Transaction
    ): Result<Unit> {
        return try {
            if (transaction.tid.isEmpty()) {
                return Result.failure(Exception("트랜잭션 ID가 없습니다"))
            }

            val transactionRef = db.collection("transactions_group")
                .document(gid)
                .collection("transactions")
                .document(transaction.tid)

            // 트랜잭션 존재 여부 확인
            val exists = transactionRef.get().await().exists()
            if (!exists) {
                return Result.failure(Exception("해당 트랜잭션을 찾을 수 없습니다"))
            }

            // 트랜잭션 저장
            transactionRef.set(transaction.toMap()).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTransaction(
        gid: String,
        tid: String
    ): Result<Unit> {
        return try {
            val transactionRef = db.collection("transactions_group")
                .document(gid)
                .collection("transactions")
                .document(tid)


            transactionRef.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTransactions(
        gid: String,
        verified: Boolean
    ): Result<List<Transaction>> {
        return try {
            val transactionsRef = db.collection("transactions_group")
                .document(gid)
                .collection("transactions")
                .whereEqualTo("verified", verified)
                .orderBy("payDate", Query.Direction.DESCENDING)

            val querySnapshot = transactionsRef.get().await()

            // 트랜잭션 리스트로 변환
            val transactions = querySnapshot.documents.mapNotNull { doc ->
                doc.data?.let { Transaction.fromMap(it) }
            }

            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTransactionsByDate(
        gid: String,
        date: Long
    ): Result<List<Transaction>> {
        return try {

            val startOfDay = DateUtils.getStartOfDay(date)
            val endOfDay = DateUtils.getEndOfDay(date)

            // Firestore 쿼리 생성, 내역 중 해당 일 안에 있는 것들
            val transactionsRef = db.collection("transactions_group")
                .document(gid)
                .collection("transactions")
                .whereEqualTo("verified", true)
                .whereGreaterThanOrEqualTo("payDate", startOfDay)
                .whereLessThanOrEqualTo("payDate", endOfDay)
                .orderBy("payDate", Query.Direction.DESCENDING)

            val querySnapshot = transactionsRef.get().await()

            // 트랜잭션 리스트로 변환
            val transactions = querySnapshot.documents.mapNotNull { doc ->
                doc.data?.let { Transaction.fromMap(it) }
            }

            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTransactionsByMonth(
        gid: String,
        yearMonth: Long
    ): Result<List<Transaction>> {
        return try {

            val startOfMonth = DateUtils.getStartOfMonth(yearMonth)
            val endOfMonth = DateUtils.getEndOfMonth(yearMonth)

            // Firestore 쿼리 생성, 내역 중 해당 월 안에 있는 것들
            val transactionsRef = db.collection("transactions_group")
                .document(gid)
                .collection("transactions")
                .whereEqualTo("verified", true)
                .whereGreaterThanOrEqualTo("payDate", startOfMonth)
                .whereLessThanOrEqualTo("payDate", endOfMonth)

            val querySnapshot = transactionsRef.get().await()

            // 트랜잭션 리스트로 변환
            val transactions = querySnapshot.documents.mapNotNull { doc ->
                doc.data?.let { Transaction.fromMap(it) }
            }

            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategory(gid: String): Result<Category> {
        return try {
            val categoryDoc = db.collection("categories")
                .document(gid)
                .get()
                .await()

            val category = categoryDoc.data?.let { Category.fromMap(it) } ?: Category(gid = gid, category = emptyList())

            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveCategory(gid: String, category: Category): Result<Unit> {
        return try {
            val categoryData = category.toMap()

            db.collection("categories")
                .document(gid)
                .set(categoryData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}