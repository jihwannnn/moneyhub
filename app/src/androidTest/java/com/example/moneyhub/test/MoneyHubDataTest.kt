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

@RunWith(AndroidJUnit4::class)
class MoneyHubDataTest {
    // Repository 구현체들을 직접 초기화
    private val groupRepository = GroupRepositoryImpl()
    private val boardRepository = BoardRepositoryImpl()
    private val transactionRepository = TransactionRepositoryImpl()

    private val users = listOf(
        CurrentUser(
            id = "3XagH6OFsfVLB6P0LOEtivSkzIA3",
            name = "Jihwan",
            currentGid = "",
            currentGname = "",
            role = Role.OWNER
        ),
        CurrentUser(
            id = "lStMhr46JJVkX11oEw9JRQEhFZB3",
            name = "Hee",
            currentGid = "",
            currentGname = "",
            role = Role.MANAGER
        ),
        CurrentUser(
            id = "a8Cnz09pUjdqm51iNx0PJzchIV33",
            name = "황성민",
            currentGid = "",
            currentGname = "",
            role = Role.MANAGER
        ),
        CurrentUser(
            id = "c8hS1NDwToh47fObHLZS84lRbYL2",
            name = "Min",
            currentGid = "",
            currentGname = "",
            role = Role.REGULAR
        )
    )

    @Test
    fun createDummyData() = runBlocking {

        try {
            println("Starting dummy data creation...")

            // 1. Create group with Jihwan as owner
//            println("Creating group...")
//            val createGroupResult = groupRepository.createGroup("MP", users[0])
//            requireNotNull(createGroupResult.getOrNull()) { "Group creation failed" }

            // Get created group's ID from user's groups
            val userGroupResult = groupRepository.getUserGroups(users[0].id)
            val userGroup =
                requireNotNull(userGroupResult.getOrNull()) { "Failed to get user groups" }
            val groupId = userGroup.groups.entries.find { it.value == "MP" }?.key
                ?: error("Could not find group with name 'MP'")

            // 2. Add other members to the group
            println("Adding members...")
            users.drop(1).forEach { user ->
                val joinResult = groupRepository.joinGroup(groupId, user)
                require(joinResult.getOrNull() == true) { "Failed to add member: ${user.name}" }
            }

            println("Promoting members to managers...")
            users.slice(1..2).forEach { user ->
                // 먼저 각 유저의 멤버십 정보를 가져옴
                val membershipResult = groupRepository.getUserMembership(groupId, user.id)
                val membership = requireNotNull(membershipResult.getOrNull()) { "Failed to get membership for: ${user.name}" }

                // 멤버십 정보를 바탕으로 매니저로 승급
                val promoteResult = groupRepository.promoteMember(membership, users[0])  // users[0]는 OWNER(Jihwan)
                requireNotNull(promoteResult.getOrNull()) { "Failed to promote member: ${user.name}" }
                println("Successfully promoted ${user.name} to manager")
            }

            // 3. Create posts and comments for each user
            println("Creating posts and comments...")
            users.forEach { user ->
                val post = Post(
                    gid = groupId,
                    title = "${user.name}'s Post",
                    content = "This is a test post by ${user.name}",
                    authorId = user.id,
                    authorName = user.name
                )
                val postResult = boardRepository.createPost(post)
                requireNotNull(postResult.getOrNull()) { "Failed to create post for ${user.name}" }

                // Get posts to find the created post's ID
                val postsResult = boardRepository.getPosts(groupId)
                val posts = requireNotNull(postsResult.getOrNull()) { "Failed to get posts" }
                val createdPost = posts.find { it.authorId == user.id }
                    ?: error("Could not find created post")

                val comment = Comment(
                    gid = groupId,
                    pid = createdPost.pid,
                    content = "Self comment by ${user.name}",
                    authorId = user.id,
                    authorName = user.name
                )
                val commentResult = boardRepository.addComment(comment)
                requireNotNull(commentResult.getOrNull()) { "Failed to create comment for ${user.name}" }
            }

            // 4. Create transactions for each user
            println("Creating transactions...")

            var p1 = 50000L
            var p2 = 100000L

            users.filter { it.role != Role.REGULAR }.forEach { user ->
                // Verified transaction (실제 내역)
                val verifiedTransaction = Transaction(
                    gid = groupId,
                    title = "${user.name}'s expense",
                    category = "교통비",
                    type = false,
                    amount = p1,
                    verified = true,
                    authorId = user.id,
                    authorName = user.name
                )
                val verifiedResult = transactionRepository.addTransaction(groupId, verifiedTransaction)
                requireNotNull(verifiedResult.getOrNull()) { "Failed to create verified transaction for ${user.name}" }

                // Unverified transaction (예산)
                val unverifiedTransaction = Transaction(
                    gid = groupId,
                    title = "${user.name}'s budget",
                    category = "식비",
                    type = false,
                    amount = p2,
                    verified = false,
                    authorId = user.id,
                    authorName = user.name
                )
                val unverifiedResult = transactionRepository.addTransaction(groupId, unverifiedTransaction)
                requireNotNull(unverifiedResult.getOrNull()) { "Failed to create unverified transaction for ${user.name}" }
                p1 += p1
                p2 += p2
                println("Successfully created transactions for ${user.name}")
            }

            // 5. Create categories
            println("Creating categories...")
            val category = Category(
                gid = groupId,
                category = listOf("교통비", "식비", "여가비")
            )
            val categoryResult = transactionRepository.saveCategory(groupId, category)
            requireNotNull(categoryResult.getOrNull()) { "Failed to create categories" }

            println("Successfully created all dummy data!")
            println("Group ID: $groupId")

        } catch (e: Exception) {
            println("Error creating dummy data: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}

