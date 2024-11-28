package com.example.moneyhub.data.repository.group


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.moneyhub.model.Membership
import com.example.moneyhub.model.Role
import com.example.moneyhub.model.UserGroup

class GroupRepositoryImpl : GroupRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override suspend fun createGroup(name: String): Result<Unit> {
        TODO()
    }

    override suspend fun joinGroup(
        gid: String,
        inviteCode: String
    ): Result<Boolean> {
        TODO()
    }

    override suspend fun deleteGroup(gid: String): Result<Unit> {
        TODO()
    }

    override suspend fun getUserGroups(uid: String): Result<UserGroup> {
        TODO()
    }

    override suspend fun getGroupMembers(gid: String): Result<List<Membership>> {
        TODO()
    }

    override suspend fun promoteMember(membership: Membership): Result<Unit> {
        TODO()
    }

    override suspend fun demoteMember(membership: Membership): Result<Unit> {
        TODO()
    }

    override suspend fun leaveGroup(
        gid: String,
        uid: String
    ): Result<Unit> {
        TODO()
    }
}