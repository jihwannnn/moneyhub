package com.example.moneyhub.data.repository.group

import com.example.moneyhub.model.Group
import com.example.moneyhub.model.Member
import com.example.moneyhub.model.Role

interface GroupRepository {
    suspend fun createGroup(name: String): Result<String> // gid 반환
    suspend fun joinGroup(inviteCode: String): Result<Unit>
    suspend fun deleteGroup(gid: String): Result<Unit>
    suspend fun getUserGroups(uid: String): Result<List<Group>>
    suspend fun getGroupMembers(gid: String): Result<List<Member>>
    suspend fun promoteMember(gid: String, uid: String, newRole: Role): Result<Unit>
    suspend fun demoteMember(gid: String, uid: String): Result<Unit>
    suspend fun removeMember(gid: String, uid: String): Result<Unit>
    suspend fun leaveGroup(gid: String): Result<Unit>
}