package com.example.moneyhub.data.repository.group


import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.Group
import com.example.moneyhub.model.Membership
import com.example.moneyhub.model.Role
import com.example.moneyhub.model.UserGroup
import javax.inject.Inject

class TestGroupRepository @Inject constructor() : GroupRepository {
    override suspend fun createGroup(name: String, user: CurrentUser): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun joinGroup(
        gid: String,
        user: CurrentUser,
        inviteCode: String
    ): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun deleteGroup(gid: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getUserGroups(uid: String): Result<UserGroup> {
        val userGroup = UserGroup()
        return Result.success(userGroup)
    }

    override suspend fun getGroupMembers(gid: String): Result<List<Membership>> {
        return Result.success(emptyList())
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