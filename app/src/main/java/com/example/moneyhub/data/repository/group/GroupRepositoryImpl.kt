package com.example.moneyhub.data.repository.group


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.moneyhub.model.Group
import com.example.moneyhub.model.Member
import com.example.moneyhub.model.Role

class GroupRepositoryImpl : GroupRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override suspend fun createGroup(name: String): Result<String> {
        TODO()
    }

    override suspend fun joinGroup(inviteCode: String): Result<Unit> {
        TODO()
    }

    override suspend fun deleteGroup(gid: String): Result<Unit> {
        TODO()
    }

    override suspend fun getUserGroups(uid: String): Result<List<Group>> {
        TODO()
    }

    override suspend fun getGroupMembers(gid: String): Result<List<Member>> {
        TODO()
    }

    override suspend fun promoteMember(gid: String, uid: String, newRole: Role): Result<Unit> {
        TODO()
    }

    override suspend fun demoteMember(gid: String, uid: String): Result<Unit> {
        TODO()
    }

    override suspend fun removeMember(gid: String, uid: String): Result<Unit> {
        TODO()
    }

    override suspend fun leaveGroup(gid: String): Result<Unit> {
        TODO()
    }
}