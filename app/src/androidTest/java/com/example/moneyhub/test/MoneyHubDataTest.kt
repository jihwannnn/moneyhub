package com.example.moneyhub.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.moneyhub.test.repo.AuthRepositoryImpl
import com.example.moneyhub.test.repo.BoardRepositoryImpl
import com.example.moneyhub.test.repo.GroupRepositoryImpl
import com.example.moneyhub.test.repo.TransactionRepositoryImpl
import com.example.moneyhub.model.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

@RunWith(AndroidJUnit4::class)
class MoneyHubDataTest {
    // Repository 구현체들을 직접 초기화
    private val groupRepository = GroupRepositoryImpl()
    private val boardRepository = BoardRepositoryImpl()
    private val transactionRepository = TransactionRepositoryImpl()

    val currentUsers = listOf(
        CurrentUser(
            id = "C91y7ewYEuQulCowAWHFVCchvg12",
            name = "황성민",
            currentGid = "X04xos2gjtmbf8xzfMUR",
            currentGname = "Student Council",
            role = Role.OWNER
        ),
        CurrentUser(
            id = "VWFp1gBD5DhqDtkcwkASVDZOM5S2",
            name = "Jihwan",
            currentGid = "k7aswVQHBPNnRdhvSvVl",
            currentGname = "Basketball club",
            role = Role.OWNER
        ),
        CurrentUser(
            id = "UgaiURq3E7WBVX4I9SmPBIbMITG2",
            name = "Huijin",
            currentGid = "wXaGFvTxwimWfWiwCg2e",
            currentGname = "Board Game Club",
            role = Role.OWNER
        )
    )


    @Test
    fun addUsersToGroups() = runBlocking {
        // 가입시킬 사용자들
        val userIds = listOf(
            "kRDZDiefIVUY9a2lfIn2cx2P3fA3",
            "G5tKU9UFWDaYv4RG69DKq90lCFm2",
            "e2TJiWlkEcQWpO3QU2w9BVkCesV2",
            "yQzwqSZpdYSlML6odGkOcnVsh0j1",
            "DdZ1A9rjUNXPJofAVyTQoPSR0Ge2",
            "5R0ei2tOiMfMXMB7sW5TbtNdZyv1",
            "dcdcKlx7pAUSznnncTQSZoRFBQs2",
            "fUcz75fEjYRMa9DnSFa7wpeVsb92"
        )

        // 실제 그룹 ID들
        val groupIds = listOf(
            "X04xos2gjtmbf8xzfMUR",    // Student Council
            "k7aswVQHBPNnRdhvSvVl",    // Basketball club
            "wXaGFvTxwimWfWiwCg2e"     // Board Game Club
        )


        try {
            userIds.forEach { uid ->
                println("Adding user $uid to groups...")

                groupIds.forEach { gid ->
                    val user = CurrentUser(
                        id = uid,
                        name = "User_$uid".take(10),
                        currentGid = "",
                        currentGname = "",
                        role = Role.REGULAR
                    )

                    val joinResult = groupRepository.joinGroup(gid, user)
                    when {
                        joinResult.isSuccess -> println("Successfully added user $uid to group $gid")
                        joinResult.isFailure -> println("Failed to add user $uid to group $gid: ${joinResult.exceptionOrNull()?.message}")
                    }
                }
            }

            println("Successfully added all users to all groups!")

        } catch (e: Exception) {
            println("Error adding users to groups: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }



    @Test
    fun createTransactionsForGroups() = runBlocking {

        try {
            // 그룹별 추가 카테고리 정의
            val groupCategories = mapOf(
                "X04xos2gjtmbf8xzfMUR" to listOf(
                    "회의비", "행사비", "복지사업", "홍보비", "비품비",
                    "학생회비", "지각비", "행사수익"  // 수입용 추가
                ), // Student Council
                "k7aswVQHBPNnRdhvSvVl" to listOf(
                    "대회비", "장비비", "훈련비", "대관료", "단체복",
                    "동아리비", "지각비", "대회상금"  // 수입용 추가
                ), // Basketball club
                "wXaGFvTxwimWfWiwCg2e" to listOf(
                    "게임구매", "간식비", "대회상금", "임대료", "이벤트비",
                    "동아리비", "지각비", "참가비"  // 수입용 추가
                )  // Board Game Club
            )

            val defaultCategories = listOf("회식비", "교통비", "물품비", "기타")


            // 각 그룹별로 처리
            groupCategories.forEach { (gid, additionalCategories) ->
                // 카테고리 저장
                val allCategories = defaultCategories + additionalCategories
                val category = Category(
                    gid = gid,
                    category = allCategories
                )
                transactionRepository.saveCategory(gid, category)
                println("Saved categories for group $gid")

                listOf(10, 11, 12).forEach { month ->
                    // 지출 10개 생성
                    repeat(10) {
                        val randomDay = (1..28).random()
                        val date = DateUtils.dateToMillis("2024-${month}-${randomDay}") ?: 0
                        val randomCategory = allCategories.random()

                        // 카테고리별 의미있는 금액 범위 설정
                        val amount = when (randomCategory) {
                            "회식비" -> (200000L..500000L).random()
                            "교통비" -> (30000L..100000L).random()
                            "물품비" -> (50000L..200000L).random()
                            "회의비" -> (100000L..300000L).random()
                            "행사비", "대회비" -> (500000L..2000000L).random()
                            "복지사업" -> (1000000L..3000000L).random()
                            "홍보비" -> (200000L..800000L).random()
                            "비품비", "장비비" -> (100000L..500000L).random()
                            "훈련비", "대관료" -> (300000L..700000L).random()
                            "단체복" -> (400000L..800000L).random()
                            "게임구매" -> (50000L..150000L).random()
                            "간식비" -> (30000L..100000L).random()
                            "임대료" -> (500000L..1000000L).random()
                            "이벤트비" -> (200000L..600000L).random()
                            else -> (50000L..200000L).random()
                        }

                        val transaction = Transaction(
                            gid = gid,
                            title = "${month}월 ${randomCategory} 지출",
                            category = randomCategory,
                            type = false, // 지출
                            amount = amount,
                            content = "${month}월 ${randomDay}일 ${randomCategory} 관련 지출",
                            payDate = date,
                            verified = true,
                            authorId = currentUsers.find { it.currentGid == gid }?.id ?: "",
                            authorName = currentUsers.find { it.currentGid == gid }?.name ?: ""
                        )
                        transactionRepository.addTransaction(gid, transaction)
                    }

                    repeat(5) {
                        val randomDay = (1..28).random()
                        val date = getDateMillis(2024, month, randomDay)
                        val randomCategory = allCategories.random()

                        val amount = when (randomCategory) {
                            "학생회비", "동아리비" -> (30000L..50000L).random() * (10..20).random() // 인당 3-5만원 * 10-20명
                            "지각비" -> (5000L..10000L).random() * (3..8).random() // 인당 5-10천원 * 3-8명
                            "행사수익" -> (500000L..1500000L).random()
                            "대회상금" -> (1000000L..3000000L).random()
                            "참가비" -> (10000L..20000L).random() * (5..15).random() // 인당 1-2만원 * 5-15명
                            else -> (500000L..1500000L).random()
                        }

                        val incomeTitle = when (randomCategory) {
                            "학생회비", "동아리비" -> "${month}월 ${randomCategory} 납부"
                            "지각비" -> "${month}월 ${randomDay}일 ${randomCategory} 납부"
                            "행사수익" -> "${month}월 행사 수익금"
                            "대회상금" -> "${month}월 대회 상금"
                            "참가비" -> "${month}월 이벤트 참가비"
                            else -> "${month}월 ${randomCategory} 수입"
                        }

                        val incomeContent = when (randomCategory) {
                            "학생회비", "동아리비" -> "${month}월 ${randomCategory} 일괄 납부"
                            "지각비" -> "${month}월 ${randomDay}일 지각자 벌금"
                            "행사수익" -> "${month}월 행사 진행 수익금"
                            "대회상금" -> "${month}월 대회 수상 상금"
                            "참가비" -> "${month}월 이벤트 참가비 수입"
                            else -> "${month}월 ${randomCategory} 관련 수입"
                        }

                        val transaction = Transaction(
                            gid = gid,
                            title = incomeTitle,
                            category = randomCategory,
                            type = true, // 수입
                            amount = amount,
                            content = incomeContent,
                            payDate = date,
                            verified = true,
                            authorId = currentUsers.find { it.currentGid == gid }?.id ?: "",
                            authorName = currentUsers.find { it.currentGid == gid }?.name ?: ""
                        )
                        transactionRepository.addTransaction(gid, transaction)
                    }
                }
                println("Created transactions for group $gid")
            }

            println("Successfully created all transactions!")

        } catch (e: Exception) {
            println("Error creating transactions: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    // 날짜를 밀리초로 변환하는 헬퍼 함수
    private fun getDateMillis(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day)
        return calendar.timeInMillis
    }
}

