package com.example.moneyhub.test.repo


import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.Group
import com.example.moneyhub.model.Membership
import com.example.moneyhub.model.Role
import com.example.moneyhub.model.UserGroup
import javax.inject.Inject

class TestGroupRepository @Inject constructor() : GroupRepository {

    private val testGroups = mapOf(
        "group1" to "가족 모임",
        "group2" to "회사 동호회",
        "group3" to "친구 여행"
    )

    private val testMembers = mapOf(
        "group1" to listOf(
            Membership("me", "group1", "나", Role.MANAGER),
            Membership("user1", "group1", "김회장", Role.OWNER),
            Membership("user2", "group1", "박매니저", Role.MANAGER),
            Membership("user3", "group1", "이멤버", Role.REGULAR)
        ),
        "group2" to listOf(
            Membership("me", "group1", "나", Role.MANAGER),
            Membership("user2", "group2", "박매니저", Role.OWNER),
            Membership("user1", "group2", "김회장", Role.REGULAR),
            Membership("user3", "group2", "이멤버", Role.REGULAR)
        ),
        "group3" to listOf(
            Membership("me", "group1", "나", Role.MANAGER),
            Membership("user3", "group3", "이멤버", Role.OWNER),
            Membership("user1", "group3", "김회장", Role.MANAGER),
            Membership("user2", "group3", "박매니저", Role.REGULAR)
        )
    )

    override suspend fun createUserGroup(uid: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun createGroup(name: String, user: CurrentUser): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun joinGroup(
        gid: String,
        user: CurrentUser
    ): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun deleteGroup(gid: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getUserGroups(uid: String): Result<UserGroup> {
        return Result.success(UserGroup(uid, testGroups))
    }

    override suspend fun getUserMembership(gid: String, uid: String): Result<Membership> {
        return testMembers[gid]?.find { it.uid == uid }?.let {
            Result.success(it)
        } ?: Result.failure(Exception("멤버십을 찾을 수 없습니다"))
    }

    override suspend fun getGroupMembers(gid: String): Result<List<Membership>> {
        return Result.success(testMembers[gid] ?: emptyList())
    }

    override suspend fun promoteMember(membership: Membership, user: CurrentUser): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun demoteMember(membership: Membership): Result<Unit> {
        return Result.success(Unit)
    }


    override suspend fun leaveGroup(
        gid: String,
        user: CurrentUser
    ): Result<Unit> {
        return Result.success(Unit)
    }
}