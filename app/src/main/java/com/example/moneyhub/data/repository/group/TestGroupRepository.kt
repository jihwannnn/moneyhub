package com.example.moneyhub.data.repository.group


import com.example.moneyhub.data.model.Group
import com.example.moneyhub.data.model.Member
import com.example.moneyhub.data.model.Role
import javax.inject.Inject

class TestGroupRepository @Inject constructor() : GroupRepository {
    override suspend fun createGroup(name: String): Result<String> {
        return Result.success("")
    }

    override suspend fun joinGroup(inviteCode: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun deleteGroup(gid: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getUserGroups(uid: String): Result<List<Group>> {
        return Result.success(emptyList())
    }

    override suspend fun getGroupMembers(gid: String): Result<List<Member>> {
        return Result.success(emptyList())
    }

    override suspend fun promoteMember(gid: String, uid: String, newRole: Role): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun demoteMember(gid: String, uid: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun removeMember(gid: String, uid: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun leaveGroup(gid: String): Result<Unit> {
        return Result.success(Unit)
    }
}