package com.example.moneyhub.data.repository.group

import com.example.moneyhub.model.Membership
import com.example.moneyhub.model.Role
import com.example.moneyhub.model.UserGroup

interface GroupRepository {
    suspend fun createGroup(name: String): Result<Unit>
    suspend fun joinGroup(gid: String, inviteCode: String): Result<Boolean>
    suspend fun deleteGroup(gid: String): Result<Unit>
    suspend fun getUserGroups(uid: String): Result<UserGroup>
    suspend fun getGroupMembers(gid: String): Result<List<Membership>>
    suspend fun promoteMember(membership: Membership): Result<Unit>
    suspend fun demoteMember(membership: Membership): Result<Unit>
    suspend fun leaveGroup(gid: String, uid: String): Result<Unit>
}