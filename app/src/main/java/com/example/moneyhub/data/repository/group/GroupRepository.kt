package com.example.moneyhub.data.repository.group

import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.Membership
import com.example.moneyhub.model.Role
import com.example.moneyhub.model.UserGroup

interface GroupRepository {

    // 유저 그룹 생성
    suspend fun createUserGroup(uid: String): Result<Unit>

    // 그룹 생성
    suspend fun createGroup(name: String, user: CurrentUser): Result<Unit>

    // 그룹 참여
    suspend fun joinGroup(gid: String, user: CurrentUser): Result<Boolean>

    // 그룹 삭제
    suspend fun deleteGroup(gid: String, user: CurrentUser): Result<Unit>

    // 특정 유저가 가입한 그룹 가져오기
    suspend fun getUserGroups(uid: String): Result<UserGroup>

    // 특정 유저의 특정 그룹 멤버쉽 가져오기
    suspend fun getUserMembership(gid: String, uid: String): Result<Membership>

    // 특정 그룹의 멤버쉽 가져오기
    suspend fun getGroupMembers(gid: String): Result<List<Membership>>

    // 멤버 승급
    suspend fun promoteMember(membership: Membership, user: CurrentUser): Result<Unit>

    // 멤버 강등
    suspend fun demoteMember(membership: Membership): Result<Unit>

    // 그룹 떠나기
    suspend fun leaveGroup(gid: String, user: CurrentUser): Result<Unit>
}