package com.example.moneyhub.data.repository.transaction

import com.example.moneyhub.model.Category
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.moneyhub.model.Transaction
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class TransactionRepositoryImpl : TransactionRepository {
    private val auth = FirebaseAuth.getInstance()
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

            // 저장
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

            // 트랜잭션 업데이트
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

            // Calendar를 사용하여 해당 날짜의 시작과 끝 시간 계산
            val calendar = Calendar.getInstance().apply {
                timeInMillis = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = calendar.timeInMillis

            calendar.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            val endOfDay = calendar.timeInMillis

            // Firestore 쿼리 생성
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
            // Calendar를 사용하여 해당 월의 시작과 끝 시간 계산
            val calendar = Calendar.getInstance().apply {
                timeInMillis = yearMonth
                set(Calendar.DAY_OF_MONTH, 1) // 월의 첫째 날
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val startOfMonth = calendar.timeInMillis

            calendar.apply {
                add(Calendar.MONTH, 1) // 다음 달의 첫째 날
                add(Calendar.MILLISECOND, -1) // 현재 달의 마지막 순간
            }
            val endOfMonth = calendar.timeInMillis

            // Firestore 쿼리 생성
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
            // groups 컬렉션에서 해당 그룹의 카테고리 문서 조회
            val categoryDoc = db.collection("category")
                .document(gid)
                .get()
                .await()

            // 문서가 존재하면 데이터 변환하여 반환
            if (categoryDoc.exists()) {
                val category = categoryDoc.data?.let { Category.fromMap(it) } ?: Category(gid = gid)
                Result.success(category)
            } else {
                // 문서가 없으면 빈 카테고리 목록 반환
                Result.success(Category(gid = gid))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveCategory(gid: String, category: Category): Result<Unit> {
        return try {
            // 카테고리 데이터를 Map 형태로 변환
            val categoryData = category.toMap()

            // groups 컬렉션의 해당 그룹 문서 아래에 카테고리 저장
            db.collection("groups")
                .document(gid)
                .collection("settings")
                .document("categories")
                .set(categoryData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}